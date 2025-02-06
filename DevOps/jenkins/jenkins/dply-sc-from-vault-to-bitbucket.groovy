import hudson.util.Secret
import groovy.json.StringEscapeUtils;

pipeline {
    agent {
        kubernetes {
			yaml '''
apiVersion: v1
kind: Pod
spec:
  activeDeadlineSeconds: 16000
  securityContext:
    fsGroup: 1000
  volumes:
    - name: "sshkey"
      secret:
        defaultMode: 384
        secretName: "deployment-jenkins-ssh"
  containers:
    - name: jnlp
      image: artifactory.rxpdev.com/pipeline/gp-jenkins-agent-deployer:latest
      tty: true
      resources:
        limits:
          cpu: "4000m"
          memory: "4Gi"
        requests:
          cpu: "1000m"
          memory: "2Gi"
      volumeMounts:
        - mountPath: "/etc/ssh/keys"
          name: "sshkey"
          readOnly: true
            '''
        }
    }

        options {
            timeout(time: 500, unit: 'MINUTES')
        }
        parameters {
            string(name: 'rcName', description: 'Release Candidate Name')
            choice(name: 'vaultEnvironment', choices: ['vault_dev', 'vault_dev_azure', 'vault_dev_gke', 'vault_cert_ncde_w1', 'vault_cert_ncde_w3', 'vault_staging_w1', 'vault_staging_w3', 'vault_cert_cde_w1', 'vault_cert_cde_w3', 'vault_prod_cde_w1', 'vault_prod_cde_w3', 'vault_sc', 'vault_cw', 'vault_ne_prod', 'vault_we_prod', 'vault_we_preprod', 'vault_ne_preprod', 'vault_dev_portico', 'vault_sc_portico', 'vault_cw_portico', 'vault_ne_prod_portico', 'vault_we_prod_portico', 'vault_we_preprod_portico', 'vault_ne_preprod_portico', 'vault_dev_sharedservices', 'vault_sc_sharedservices', 'vault_cw_sharedservices', 'vault_ne_prod_sharedservices', 'vault_we_prod_sharedservices', 'vault_we_preprod_sharedservices', 'vault_ne_preprod_sharedservices'], description: 'Vault Environment')
        }

        environment {
            PIPELINE_CONFIG_CONSUL_URL = consul.getConsulURL()
            VAULT_URL = vaultHelper.getVaultURL()
            VAULT_PATH = 'secret'
            VAULT_ROLE_ID = vault path: 'secret/pipeline/vault', key: 'role-id', engineVersion: VAULT_ENGINE_VERSION
            VAULT_SECRET_ID = vault path: 'secret/pipeline/vault', key: 'secret-id', engineVersion: VAULT_ENGINE_VERSION
            BITBUCKET_HOST = consul.getPipelineConfig(PIPELINE_CONFIG_CONSUL_URL, "BITBUCKET_HOST")
            DISPLAY_NAME = "# ${currentBuild.number} ${rcName} ${vaultEnvironment}"
        }

        stages {
            stage('Get Environment from URL') {
                steps {
                    script {
                        currentBuild.displayName = "${env.DISPLAY_NAME}"
                        // Extract environment
                        environment = extractEnvironmentFromURL("${VAULT_URL}")
                        echo "Environment is: ${environment}"
                    }
                }
            }

        stage('Identify Target Folders') {
            steps {
                script {
                    // Get Vault token
                    retry(10) {
                        VAULT_TOKEN = vaultHelper.getVaultToken(VAULT_URL, params.vaultEnvironment)

                        sleep(time:10, unit:'SECONDS')
                    }

                    // Initial folder list
                    def stack = []
                    def foldersWithKeys = []
                    def initialResponse = sh(script: """curl -k --header "X-Vault-Token: $VAULT_TOKEN" --request GET $VAULT_URL/v1/$VAULT_PATH/metadata/?list=true""", returnStdout: true).trim()
                    def initialJsonResponse = readJSON text: initialResponse

                    // Null check
                    if (initialJsonResponse.data != null && initialJsonResponse.data.keys != null) {
                        initialJsonResponse.data.keys.each {
                            if (!it.equalsIgnoreCase('VerifySecrets/')) {
                                stack << [path: it, depth: 1]
                            }
                        }
                    }

                    while (stack.size() > 0) {
                        def currentFolderData = stack.pop()
                        def currentFolder = currentFolderData.path
                        def currentDepth = currentFolderData.depth

                        // Filter folders based on rcName
                        // Include paths that start with 'rcName/' and exclude those that start with 'rcName-'
                        if (currentFolder.startsWith("${params.rcName}/") && !currentFolder.startsWith("${params.rcName}-") || currentFolder.startsWith("${params.rcName}-acceptance/")) {
                            
                            // Fetch current folder content
                            def folderResponse = sh(script: """curl -k --header "X-Vault-Token: $VAULT_TOKEN" --request GET $VAULT_URL/v1/$VAULT_PATH/metadata/${currentFolder}?list=true""", returnStdout: true).trim()
                            def folderJsonResponse = readJSON text: folderResponse

                            // Null check
                            if (folderJsonResponse.data != null && folderJsonResponse.data.keys != null) {
                                def folderContent = folderJsonResponse.data.keys

                                def hasKeys = folderContent.any { !it.endsWith('/') }

                                if (hasKeys) {
                                    foldersWithKeys << currentFolder
                                }

                                // Add all subfolders to the stack for further processing
                                folderContent.findAll { it.endsWith('/') && !it.equalsIgnoreCase('VerifySecrets/') }.each {
                                    stack.push([path: "${currentFolder}${it}", depth: currentDepth + 1])
                                }
                            }
                        }
                    }

                    // Save the foldersWithKeys to an environment variable for next stage
                    env.FOLDERS_WITH_KEYS = foldersWithKeys.join(',')
                    echo "Folders that have keys and match the environment:\n ${env.FOLDERS_WITH_KEYS}"
                    echo "Total number of folders that match the environment and have keys: ${foldersWithKeys.size()}"
                }
            }
        }


        stage('Get Vault Secrets') {
            steps {
                script {
                    def targetFolders = env.FOLDERS_WITH_KEYS.split(',')
                    def excludedKeys = ['INTERMEDIATE_CRT', 'SSL_CRT', 'SSL_KEY']
                    def gitConfigEmail = 'deliveryacceleration@globalpay.com'
                    def gitConfigName = 'automated_platform_jenkins'
                    def namespace, tag
                    rcName = params.rcName

                    targetFolders.each { currentFolder ->
                        stage("Working on ${currentFolder}") {
                            echo "Fetching secrets for ${currentFolder}..."

                            if (!currentFolder.contains(params.rcName)) {
                                return
                            }

                            def encryptedSecretKeysList = []
                            def failedSecretKeysList = []
                            def extractedData = extractNamespaceAndTag(currentFolder)
                            namespace = extractedData.namespace
                            tag = extractedData.tag
                            
                            gsm.setGsmEnvVars(namespace, tag)

                            def folderContent = fetchFolderContent(currentFolder, excludedKeys)
                            
                            folderContent.each { secretKey ->
                                processSecretKey(secretKey, currentFolder, extractedData, rcName, encryptedSecretKeysList, failedSecretKeysList)
                            }

                            displayResults(currentFolder, encryptedSecretKeysList, failedSecretKeysList)
                            def repoDir = cloneRepo(rcName)
                            if (repoDir) {
                                def sourceSecretsDir = "secret/${currentFolder}"
                                def destBitBucketDir = "${sourceSecretsDir}"

                                boolean success = processSecretFiles(namespace, tag, rcName, gitConfigEmail, gitConfigName, repoDir, sourceSecretsDir, destBitBucketDir, currentFolder)

                                if (success) {
                                    deployAndVerifyKeys(namespace, tag, rcName, repoDir, gitConfigEmail, gitConfigName)
                                    cleanup(currentFolder)
                                } else {
                                    echo "Error processing secret files for folder ${currentFolder}. Skipping deploy and cleanup."
                                }
                            } else {
                                echo "Failed to clone repository ${rcName}. Skipping related operations."
                            }

                        }                            
                    }
                }
            }
        }
    }   
}

def extractEnvironmentFromURL(url) {
    // Localize the matcher within the function scope.
    def match = (url =~ /(?<=\.)[^.]+(?=\.k8s)/)
    if (match.find()) {
        return match[0]
    }
    return '' // return empty string if not found
}

def extractNamespaceAndTag(folder) {
    // Folder format is /namespace/tag/.../service/
    // Split the folder using '/'
    def parts = folder.split('/').findAll { it != '' }
    return [namespace: parts[0], tag: parts[1]]
}

def findRcName(String fullPath) {
    def parts = fullPath.split('/')
    return parts[-1]  // returns the last element in the array
}

def fetchSecretValue(String secretKey, String currentFolder) {
    // Construct the Vault API URL based on the folder and key
    def vaultApiUrl = "$VAULT_URL/v1/$VAULT_PATH/data/$currentFolder$secretKey"
    def secretValueResponse = sh(script: """curl -S -k --header "X-Vault-Token: $VAULT_TOKEN" --request GET $vaultApiUrl""", returnStdout: true).trim()
    // Parse the JSON response
    def secretJsonValueResponse = readJSON text: secretValueResponse

    def value = secretJsonValueResponse?.data?.data?.value
    if (value != null) {
        echo "Debug: Fetched secret value successfully."
        return value
    } else {
        echo "Debug: Failed to fetch secret value."
    }
}

class EncryptData {
   def value
   def keyUrl
}

def getValuesFromBitbucket(namespace, tag, rcName) {
    try {
        repoKeysPath = vaultHelper.generateVaultPath(namespace, tag, rcName, '')
        sh "rm -rf vault-secrets; git clone -b feature/VerifySecrets ${bitbucket.getCloneUrl(BITBUCKET_HOST)}/scrt/${rcName}.git vault-secrets"  // Modified to clone feature/VerifySecrets
        final foundFiles = sh(script: "ls -1 vault-secrets/${repoKeysPath}", returnStdout: true).split()
        keyValueMap = [:]
        for (file in foundFiles) {
            try {
                print("File: ${file}")
                key = file.replaceAll(".json", "")
                keyValue = readFile("vault-secrets/${repoKeysPath}/${file}")
                keyValueMap[key] = keyValue
            } catch (FileNotFoundException e) {
                print("File not found: ${file}. ${e.getMessage()}")
                // Handle the exception as needed, e.g., continue to the next file, log the error, etc.
            }
        }
        return keyValueMap
    } catch (Exception e) {
        print("An error occurred: ${e.getMessage()}")
        // Handle the general exception as needed
        return null
    }
}

def processVaultKey(keyName, keyValue, namespace, tag, rcName) {      
    jsonObj = readJSON text: new String (keyValue)
    fullDecryptedString = ''
    sslCertificateKeyNames = ['SSL_KEY', 'SSL_CRT', 'INTERMEDIATE_CRT', 'CERTIFICATE_CRT', 'CERTIFICATE_KEY', 'KEK_KEY', 'GCP_PRIVATE_KEY']

    encryptedValue = jsonObj['value'].trim()
    decryptedValue = gsm.decrypt(projectIdEncryption, location, keyring, kmsKeyName, encryptedValue, gsmToken)
    decryptedValue_decoded = new String(decryptedValue.decodeBase64())

    if (!sslCertificateKeyNames.contains(keyName)) {
        fullDecryptedString = StringEscapeUtils.escapeJava(decryptedValue_decoded).trim()
    } else {
        fullDecryptedString = decryptedValue_decoded.trim()
    }

    if(VAULT_URL == 'https://vault.service.rxpdev.auto:8200') {
        deploySecretToVault(VAULT_URL, params.vaultEnvironment, namespace, tag, rcName, keyName, fullDecryptedString)
    } else if((params.vaultEnvironment == 'vault_cw') || (params.vaultEnvironment == 'vault_sc')) {
        VAULT_URL = consul.getPipelineConfig(PIPELINE_CONFIG_CONSUL_URL, vaultEnvironment.toUpperCase() + "_HOST")
        deploySecretToVault(VAULT_URL, params.vaultEnvironment, namespace, tag, rcName, keyName, fullDecryptedString)
    } else {
        deploySecretToVault(VAULT_URL, params.vaultEnvironment, namespace, tag, rcName, keyName, fullDecryptedString, "")
    }
}

def deploySecretToVault(url, credentialsId, namespace, tag, rcName, key, value, proxy) {
    println "[INFO] - Attempting to deploy secret: ${key} to ${url} using credentials: ${credentialsId}"
    def secretPath = "${url}/v1/secret/data/${namespace}/${tag}/VerifySecrets/${rcName}/${key}"
    
    body = """{"data":{"value":"${value}"}}"""
    vaultResponse = httpRequest consoleLogResponseBody: false,
                                httpMode: 'POST',
                                contentType: 'APPLICATION_JSON',
                                requestBody: body,
                                url: secretPath,
                                validResponseCodes: '200,204',
                                customHeaders: [[
                                    name: 'X-Vault-Token',
                                    value: "$VAULT_TOKEN",
                                    maskValue: true
                                ]]

    if ("${vaultResponse}".contains('200') || "${vaultResponse}".contains('204')) {
        println "[INFO] - ${vaultResponse} successfully deployed ${secretPath}"
    } else {
        println "[ERROR] - Failed to deploy secret: ${key} to ${secretPath}; Response code: ${vaultResponse}"
        echo "Failed to deploy ${secretPath}; Response code: ${vaultResponse}"
    }
}

def compareVaultSecrets(String originalFolder, String testFolder) {
    def originalSecrets = fetchAllSecretsFromVaultFolder(originalFolder)
    def testSecrets = fetchAllSecretsFromVaultFolder(testFolder)
    
    allKeysMatch = true
    
    originalSecrets.each { key, originalValue ->
        def testValue = testSecrets[key]
        if (testValue == null) {
            echo "Secret $key exists in original but not in test folder"
            allKeysMatch = false
        } else if (originalValue != testValue) {
            echo "Mismatch in value for secret $key between original and test folders"
            allKeysMatch = false 
        }
    }
    
    if (allKeysMatch) {
        echo "All keys and values match. All good!"
        return true
    } else {
        error "Houston, we have a problem. Some keys or values do not match!"
        return false
    }
}

def fetchAllSecretsFromVaultFolder(String folder) {
    def allSecrets = [:]
    def folderApiUrl = "$VAULT_URL/v1/$VAULT_PATH/data/$folder"
    def folderResponse = sh(script: """curl -k --header "X-Vault-Token: $VAULT_TOKEN" --request GET $folderApiUrl""", returnStdout: true).trim()    
    // Parse the JSON response
    def folderJsonResponse = readJSON text: folderResponse
    def secretData = folderJsonResponse?.data?.data

    if (secretData != null) {
        secretData.each { key, value ->
            allSecrets[key] = value
        }
    } else {
        echo "Debug: No secrets found or failed to fetch."
    }

    return allSecrets
}

// 1st part of stage to validate secrets 
def fetchFolderContent(currentFolder, excludedKeys) {
    def folderResponse = sh(script: """curl -k --header "X-Vault-Token: $VAULT_TOKEN" --request GET $VAULT_URL/v1/$VAULT_PATH/metadata/${currentFolder}?list=true""", returnStdout: true).trim()
    def folderJsonResponse = readJSON text: folderResponse
    return folderJsonResponse.data.keys.findAll { !it.endsWith('/') && !excludedKeys.contains(it) }
}

def processSecretKey(secretKey, currentFolder, extractedData, rcName, encryptedSecretKeysList, failedSecretKeysList) {
    try {
        def secretValue = fetchSecretValue(secretKey, currentFolder)

        if (secretValue == null) {
            throw new Exception("Secret value is null for key: $secretKey")
        }

        EncryptData individualEncryptData = new EncryptData()
        //Remove single quotes from value
        secretValue = secretValue.replaceAll('\'', '')
        String encryptedValueBase64 =  Base64.getUrlEncoder().withoutPadding().encodeToString(secretValue.trim().getBytes());
        def encryptedValue = gsm.encrypt(env.projectIdEncryption, env.location, env.keyring, env.kmsKeyName, encryptedValueBase64, env.gsmToken)
        def secretId = gsm.generateSecretId(extractedData.namespace.trim(), extractedData.tag.trim(), rcName.trim(), secretKey)
        
        individualEncryptData.keyUrl = "https://cloudkms.googleapis.com/v1/${encryptedValue.name}"
        individualEncryptData.value = encryptedValue.ciphertext

        def jsonFormat = readJSON text: groovy.json.JsonOutput.toJson(individualEncryptData)
        def jsonFile = vaultHelper.generateVaultPath(extractedData.namespace.trim(), extractedData.tag.trim(), rcName.trim(), secretKey) + ".json"
        writeJSON(file: jsonFile, json: jsonFormat)

        encryptedSecretKeysList.add(secretKey)
    } catch (Exception e) {
        echo "Warning: Failed to process secret key: $secretKey - ${e.message}"
        failedSecretKeysList.add(secretKey)
    }
}


def displayResults(currentFolder, encryptedSecretKeysList, failedSecretKeysList) {
    echo "For folder ${currentFolder} - List of Encrypted Secrets:\n${encryptedSecretKeysList.join('\n')}"
    echo "For folder ${currentFolder} - Number of encrypted secrets: ${encryptedSecretKeysList.size()}"

    if (failedSecretKeysList.size() > 0) {
        echo "For folder ${currentFolder} - List of Failed Secrets:\n${failedSecretKeysList.join('\n')}"
        echo "For folder ${currentFolder} - Number of failed secrets: ${failedSecretKeysList.size()}"
    } else {
        echo "For folder ${currentFolder} - No failed secrets."
    }
}

// 2nd part of stage with push secrets
def setupGitConfig(String email, String name) {
    sh "git config user.email '${email}'"
    sh "git config user.name '${name}'"
}

def cloneRepo(String rcName) {
    try {
        sh(script: "rm -rf ${rcName}; git clone ${bitbucket.getCloneUrl(BITBUCKET_HOST)}/scrt/${rcName}.git ${rcName}")
        return rcName
    } catch (Exception e) {
        echo "Failed to clone repository: ${e.getMessage()}"
        return null
    }
}

def processSecretFiles(String namespace, String tag, String rcName, String gitConfigEmail, String gitConfigName, String repoDir, String sourceSecretsDir, String destBitBucketDir, String currentFolder) {
    boolean success = true
    dir(repoDir) {
        setupGitConfig(gitConfigEmail, gitConfigName)

        if (!fileExists(destBitBucketDir)) {
            echo "Destination directory ${destBitBucketDir} does not exist. Creating it..."
            sh "mkdir -p ${destBitBucketDir}"
        }

        try {
            sh(script: "cp -rf ../${sourceSecretsDir}* ${destBitBucketDir}")
            commitAndPushChanges(namespace, tag, rcName, "feature/VerifySecrets", "CB-3551 Adding secrets for release candidate")
        } catch (Exception e) {
            echo "Skipping the folder: ${currentFolder} due to an error: ${e.getMessage()}"
            success = false
        }
    }
    return success
}

def deployAndVerifyKeys(String namespace, String tag, String rcName, String repoDir, String gitConfigEmail, String gitConfigName) {
    def keyValueMap = getValuesFromBitbucket(namespace, tag, rcName)

    keyValueMap.each {
        entry -> println "[INFO] - Processing Key: $entry.key with value: Value: $entry.value"
        processVaultKey(entry.key, entry.value, namespace, tag, rcName)
    }

    println "Compare secret in test path vault with secret on original path in vault"

    def originalFolder = "${namespace}/${tag}/${rcName}/${key}"
    def testFolder = "${namespace}/${tag}/VerifySecrets/${rcName}/${key}"

    def allKeysMatch = compareVaultSecrets(originalFolder, testFolder)

    if (allKeysMatch) {
        // Merge and clean up Git branches
        mergeAndCleanup(repoDir, rcName, namespace, tag, "master", "feature/VerifySecrets", "CB-3551 Sync secrets from vault to bitbucket for ", gitConfigEmail, gitConfigName)
    } else {
        error "Pipeline halted due to mismatching keys or values in Vault."
    }
}

def commitAndPushChanges(String namespace, String tag, String rcName, String branchName, String commitPrefix) {
    def commitMessage = "${commitPrefix} ${rcName} (tag: ${tag}, namespace:${namespace}) by ${jenkinsHelper.getUserId()}"
    
    sh "git checkout -b ${branchName}"  // Creates and switches to a new branch
    sh "git add ."
    sh "git commit -m '${commitMessage}'"
    sh "git push -u origin ${branchName} --force"  // Pushes to the new branch
    sh "git status"
}

def mergeAndCleanup(String repoDir, String rcName, String namespace, String tag, String targetBranch, String sourceBranch, String commitPrefix, String gitConfigEmail, String gitConfigName) {
    dir(repoDir) {
        setupGitConfig(gitConfigEmail, gitConfigName)

        sh "git checkout ${targetBranch}"
        sh "git pull origin ${targetBranch}"
        sh "git merge ${sourceBranch} --no-commit"  // --no-commit prevents auto commit

        // Check for changes before committing
        def changes = sh(script: 'git diff HEAD --exit-code || echo "changes"', returnStdout: true).trim()
        if (changes == "changes") {
            commitAndPushChanges(namespace, tag, rcName, targetBranch, commitPrefix)
        }

        sh "git push origin ${targetBranch}"

        sh "git branch -d ${sourceBranch}"
        sh "git push origin --delete ${sourceBranch}"

        sh "git status"
    }
}

def cleanup(String currentFolder) {
    sh "rm -rf ${currentFolder}"
}