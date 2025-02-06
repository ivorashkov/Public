pipeline {
    agent any
    parameters {
        string(name: 'rcName', description: 'Release Candidate Name')
        string(name: 'namespace', description: 'Namespace for the deployment')
        string(name: 'tag', description: 'Environment tag for the deployment')
        string(name: 'key', description: 'Name of secret to encrypt')
        password(name: 'value', description: 'Plaintext value to encrypt')
        string(name: 'vaultFqdn', description: 'Vault FQDN')
    }

    environment {
        VAULT_ROLE_ID = vault path: 'secret/pipeline/vault', key: 'role-id'
        VAULT_SECRET_ID = vault path: 'secret/pipeline/vault', key: 'secret-id'
    }

    stages {
        stage ('Deploy secret to vault') {
            steps {
                script {
                    if (!params.value.getPlainText()?.trim()) {
                        error("Emply values are not allowed")
                    }
                    vaultToken = getVaultToken(params.vaultFqdn, VAULT_ROLE_ID, VAULT_SECRET_ID)
                    deploySecretToVault(params.vaultFqdn, params.namespace, params.tag, params.rcName, params.key, value, vaultToken)
                }
            }
        }
    }
}

/**
 * Functions from Shared Library
 * 
*/

//vaultHelper.groovy
/**
    This method returns the vault token for a given vault role id
    parameter vaultFqdn url of vault instance
    parameter vaultRoleId vault role id
    parameter vaultSecretId vault secret id
*/
def getVaultToken(vaultFqdn, vaultRoleId, vaultSecretId ) {
    // httpRequest plugin does not follow redirects
    return readJSON(text: sh (returnStdout: true, script: """set +x; curl -s -L -k -X POST '${vaultFqdn}/v1/auth/approle/login' --data '{
        "role_id": "${vaultRoleId}",
        "secret_id": "${vaultSecretId}"
        }'""")).auth.client_token
}

/**
 * This method deploys a value to vault
 * @param vaultFqdn url of vault instance
 * @param namespace namespace of project
 * @param tag enviroment tag
 * @param rcName application name
 * @param key key of the secret to store
 * @param value value of the secret to store
 * @param vaultToken role id access token for vault
**/
def deploySecretToVault(vaultFqdn, namespace, tag, rcName, key, value, vaultToken) {
    url = "${vaultFqdn}/v1/secret/${namespace}/${tag}/${rcName}/${key}"

    echo "INFO: Attempting to deploy ${url}"
    // Using curl as httpRequest plugin does not follow redirects
    vaultResponse =  sh (returnStdout: true, script: """set +x; curl -s -L -k -w '%{http_code}' -H "X-Vault-Token: ${vaultToken}"  \
        -H "Content-Type: application/json" \
        -X POST \
        -d '{"value":"${value}"}' \
        ${url}""")

    if (vaultResponse.contains('204')) {
        print "${vaultResponse} successfully deployed ${url}"
    } else {
        error("Failed to deploy ${url}; Response code: ${vaultResponse}")
    }
}