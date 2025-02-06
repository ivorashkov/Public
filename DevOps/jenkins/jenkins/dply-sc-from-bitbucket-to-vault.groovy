import groovy.json.StringEscapeUtils;
pipeline {
	agent any
    options {
        timestamps()
        logstash()
    }

    parameters {
        string(name: 'rcName', description: 'Release Candidate Name')
        string(name: 'rcVersion', description: 'Release Candidate Version')
        string(name: 'namespace', description: 'Namespace for the deployment')
        string(name: 'tag', description: 'Environment tag for the deployment')
        choice(name: 'vaultEnvironment', choices: ['vault_dev_gke', 'vault_cert_ncde_w1', 'vault_cert_ncde_w3', 'vault_staging_w1', 'vault_staging_w3', 'vault_cert_cde_w1', 'vault_cert_cde_w3', 'vault_shared_w1', 'vault_prod_cde_w1', 'vault_prod_cde_w3'], description: 'Vault Environment')
    }

    environment {
        PIPELINE_CONFIG_CONSUL_URL = consul.getConsulURL()
        BITBUCKET_HOST = consul.getPipelineConfig(PIPELINE_CONFIG_CONSUL_URL, "BITBUCKET_HOST")
        SECRETS_REPO = """${bitbucket.getCloneUrl(BITBUCKET_HOST)}/scrt/${rcName}.git"""
        VAULT_URL = vaultHelper.getVaultURL()
    }

    stages {
        stage("Deploy Keys from BitBucket to Vault") {
            steps {
                script {
                    gsm.setGsmEnvVars(namespace,tag) // Set Google Secret Manager environment variables
                    keyValueMap = getValuesFromBitbucket()
                    keyValueMap.each{
                        entry -> println "[INFO] - Processing Key: $entry.key with value: Value: $entry.value"
                        processVaultKey(entry.key, entry.value)
                    }
                }
            }
        }
   }
}

def getValuesFromBitbucket() {
    repoKeysPath = vaultHelper.generateVaultPath(params.namespace, params.tag, params.rcName, '')
    sh "rm -rf vault-secrets; git clone ${env.SECRETS_REPO} vault-secrets"
    final foundFiles = sh(script: "ls -1 vault-secrets/${repoKeysPath}", returnStdout: true).split()
    keyValueMap = [:]
    for (file in foundFiles) {
        print("File: ${file}")
        key = file.replaceAll(".json","")
        keyValue = readFile("vault-secrets/${repoKeysPath}/${file}")
        keyValueMap[key] = keyValue
    }
    return keyValueMap
}

def processVaultKey(keyName, keyValue) {
    jsonObj = readJSON text: new String (keyValue)
    fullDecryptedString = ''
    sslCertificateKeyNames = ['SSL_KEY', 'SSL_CRT', 'INTERMEDIATE_CRT', 'CERTIFICATE_CRT', 'CERTIFICATE_KEY', 'KEK_KEY', 'GCP_PRIVATE_KEY']

    encryptedValue = jsonObj['value'].trim()
    decryptedValue = gsm.decrypt(projectIdEncryption, location, keyring, kmsKeyName, encryptedValue, gsmToken)
    decryptedValue_decoded = new String(decryptedValue.decodeBase64())

    if (!sslCertificateKeyNames.contains(keyName)) {
        fullDecryptedString = StringEscapeUtils.escapeJava(decryptedValue_decoded)
    } else {
        fullDecryptedString = decryptedValue_decoded
    }

    vaultHelper.deploySecretToVault(VAULT_URL, params.vaultEnvironment, params.namespace, params.tag, params.rcName, keyName, fullDecryptedString, "")
}


def deploySecretToVault(url, credentialsId, namespace, tag, rcName, key, value, proxy) {
    println "VAULT_URL: ${url}"
    println "vaultEnvironment: ${credentialsId}"
    println "namespace: ${namespace}"
    println "tag: ${tag}"
    println "rcName: ${rcName}"
    println "keyName: ${key}"
    println "fullDecryptedString: ${value}"

    println "[INFO] - Attempting to deploy secret: ${key} to ${url} using credentials: ${credentialsId}"
    secretPath = "${url}/v1/secret/data/${namespace}/${tag}/${rcName}/${key}"
    
    token = vaultHelper.getVaultToken(url, credentialsId)
    println "token ${token}"

    body = """{"data":{"value":"${value}"}}"""
    vaultResponse = httpRequest consoleLogResponseBody: false,
                                httpMode: 'POST',
                                contentType: 'APPLICATION_JSON',
                                requestBody: body,
                                url: secretPath,
                                validResponseCodes: '200,204',
                                customHeaders: [[
                                    name: 'X-Vault-Token',
                                    value: token,
                                    maskValue: true
                                ]]

    if ("${vaultResponse}".contains('200') || "${vaultResponse}".contains('204')) {
        println "[INFO] - ${vaultResponse} successfully deployed ${secretPath}"
    } else {
        println "[ERROR] - Failed to deploy secret: ${key} to ${secretPath}; Response code: ${vaultResponse}"
        error("Failed to deploy ${secretPath}; Response code: ${vaultResponse}")
    }
}