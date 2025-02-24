pipeline {
    agent any

    parameters {
        string(name: 'REPO_URL', description: 'Ansible Playbook Repository URL')
        string(name: 'TARGET_HOSTS', description: 'Comma Separated list of hosts', defaultValue: '')
        string(name: 'ANSIBLE_TAGS', description: 'Ansible Tags used to limit roles executed', defaultValue: '')
        string(name: 'EXTRA_VARS', description: 'Extra variables to override in JSON format', defaultValue: '{}')
        string(name: 'PLAYBOOK_FILE', description: 'Enter Inventories Path as per Lifecycle', defaultValue: 'playbooks/dev/playbook.yml')
        string(name: 'INVENTORY_FILE', description: 'Enter Playbook Paths per Lifecycle', defaultValue: 'inventories/dev.gcp.yml')
        booleanParam(name: 'CHECK_MODE', defaultValue: true, description: 'Enable Ansible Check Mode')
    }

    options {
        timestamps()
        ansiColor('xterm')
    }

    environment {
        ANSIBLE_FORCE_COLOR = 'true'
        GOOGLE_APPLICATION_CREDENTIALS = credentials('gcp-ansible-key')
        HTTP_PROXY = squid.getSquidUrl()
        HTTPS_PROXY = squid.getSquidUrl()
        http_proxy = squid.getSquidUrl()
        https_proxy = squid.getSquidUrl()
        NO_PROXY = 'url1.com,url2.com,url3.com'
        VAULT_ENGINE_VERSION = vaultHelper.getVaultEngineVersion()
    }

    stages {
        stage('Set Lifecycle Parameters') {
            steps {
                script {
                    if (env.JENKINS_URL.contains('dev')) {
                        env.LIFECYCLE = 'dev'
                        env.REGION = 'both'
                        env.SETUP_TYPE = 'install'
                        env.SSH_KEY_NAME = "${WORKSPACE}/gpec-svcs-ansible-ssh-key"
                        env.WIN_USER = vault path: 'secret/pipeline/ANSIBLE_WINRM_USER', key: 'value', engineVersion: VAULT_ENGINE_VERSION
                        env.WIN_PASS = vault path: 'secret/pipeline/ANSIBLE_WINRM_PASSWORD', key: 'value', engineVersion: VAULT_ENGINE_VERSION
                    } else if (env.JENKINS_URL.contains('staging')) {
                        env.LIFECYCLE = 'staging'
                        env.REGION = env.JENKINS_URL.contains('euw1') ? 'west1' : 'west3'
                        env.SETUP_TYPE = 'install'
                        env.SSH_KEY_NAME = "${WORKSPACE}/gpec-svcs-ansible-ssh-key"
                        env.WIN_USER = vault path: 'secret/pipeline/ANSIBLE_WINRM_USER', key: 'value', engineVersion: VAULT_ENGINE_VERSION
                        env.WIN_PASS = vault path: 'secret/pipeline/ANSIBLE_WINRM_PASSWORD', key: 'value', engineVersion: VAULT_ENGINE_VERSION
                    } else if (env.JENKINS_URL.contains('cert-ncde')) {
                        env.LIFECYCLE = 'cert-ncde'
                        env.REGION = env.JENKINS_URL.contains('euw1') ? 'west1' : 'west3'
                        env.SETUP_TYPE = 'install'
                        env.SSH_KEY_NAME = "${WORKSPACE}/gpec-svcs-ansible-ssh-key"
                        env.WIN_USER = vault path: 'secret/pipeline/ANSIBLE_WINRM_USER', key: 'value', engineVersion: VAULT_ENGINE_VERSION
                        env.WIN_PASS = vault path: 'secret/pipeline/ANSIBLE_WINRM_PASSWORD', key: 'value', engineVersion: VAULT_ENGINE_VERSION
                    } else if (env.JENKINS_URL.contains('cert-cde')) {
                        env.LIFECYCLE = 'cert-cde'
                        env.REGION = env.JENKINS_URL.contains('euw1') ? 'west1' : 'west3'
                        env.SETUP_TYPE = 'install'
                        env.SSH_KEY_NAME = "${WORKSPACE}/gpec-svcs-ansible-ssh-key"
                        env.WIN_USER = vault path: 'secret/pipeline/ANSIBLE_WINRM_USER', key: 'value', engineVersion: VAULT_ENGINE_VERSION
                        env.WIN_PASS = vault path: 'secret/pipeline/ANSIBLE_WINRM_PASSWORD', key: 'value', engineVersion: VAULT_ENGINE_VERSION
                    } else if (env.JENKINS_URL.contains('prod-cde')) {
                        env.LIFECYCLE = 'prod-cde'
                        env.REGION = env.JENKINS_URL.contains('euw1') ? 'west1' : 'west3'
                        env.SETUP_TYPE = 'install'
                        env.SSH_KEY_NAME = "${WORKSPACE}/gpec-svcs-ansible-ssh-key"
                        env.WIN_USER = vault path: 'secret/pipeline/ANSIBLE_WINRM_USER', key: 'value', engineVersion: VAULT_ENGINE_VERSION
                        env.WIN_PASS = vault path: 'secret/pipeline/ANSIBLE_WINRM_PASSWORD', key: 'value', engineVersion: VAULT_ENGINE_VERSION
                    } else {
                        error('Invalid JENKINS_URL')
                    }
                }
            }
        }

        stage('Initialisation') {
            steps {
                dir('ansible') {
                    script {
                        def sshCredentialsId = getSSHCredentialsId()
                        if (sshCredentialsId) {
                            withCredentials([file(credentialsId: sshCredentialsId, variable: 'SSH_KEY')]) {
                                sh "mv ${SSH_KEY} ${env.SSH_KEY_NAME}"
                                sh "chmod 600 ${env.SSH_KEY_NAME}"
                            }
                        } else {
                            error "Failed to determine SSH credentials ID."
                        }
                    }
                }
            }
        }

        stage('Clone Repository') {
            steps {
                dir('ansible') {
                    script {
                        sh "git clone --branch master ${REPO_URL}"
                        script {
                            def repoName = getRepoName(REPO_URL)
                            env.REPO_NAME = repoName
                        }
                        echo "The Playbook that will be Installed is : ${env.REPO_NAME}"
                    }
                }
            }
        }

        stage('Install Ansible Dependencies') {
            steps {
                dir("ansible/${env.REPO_NAME}") {
                    sh 'ansible-galaxy install -r requirements.yml --force -v -c'
                }
            }
        }

        stage('Update Inventory & Playbook') {
            steps {
                updateInventory()
                updatePlaybook()
            }
        }

        stage('Display Information') {
            steps {
                echo "+-----------------+"
                echo "|  Target Hosts   |"
                echo "+-----------------+"
                runAnsiblePlaybookListHosts(params.EXTRA_VARS, params.TARGET_HOSTS, params.CHECK_MODE, params.ANSIBLE_TAGS)
                echo "+----------------------+-----------------------------------+"
                echo "|       Parameter      |              Value                |"
                echo "+----------------------+-----------------------------------+"
                echo "|   Repo URL           | ${params.REPO_URL.padRight(33)}|"
                echo "|   Application Name   | ${env.REPO_NAME.padRight(33)}|"
                echo "|   Inventory File     | ${params.INVENTORY_FILE.padRight(33)}|"
                echo "|   Playbook File      | ${params.PLAYBOOK_FILE.padRight(33)}|"
                echo "|   Setup Type         | ${env.SETUP_TYPE.padRight(33)}|"
                echo "|   Region             | ${env.REGION.padRight(33)}|"
                echo "|   Extra Vars         | ${params.EXTRA_VARS.padRight(33)}|"
                echo "|   Target Hosts       | ${params.TARGET_HOSTS.padRight(33)}|"
                echo "|   Tags               | ${params.ANSIBLE_TAGS.padRight(33)}|"
                echo "+----------------------+-----------------------------------+"
            }
        }

        stage('Run Ansible Playbook') {
            steps {
                script {
                    def userInput = input(id: 'confirm', message: 'Apply Playbook?', parameters: [booleanParam(defaultValue: false, description: 'Apply Playbook', name: 'CONFIRM')])
                }
                runAnsiblePlaybook(params.EXTRA_VARS, params.TARGET_HOSTS, params.CHECK_MODE, params.ANSIBLE_TAGS)
            }
        }
    }

    post {
        always {
            deleteDir()
        }
    }
}

def getRepoName(repoUrl) {
    def folderName = repoUrl.substring(repoUrl.lastIndexOf('/') + 1, repoUrl.lastIndexOf('.git'))
    return folderName
}

def runAnsiblePlaybook(extraVars, targetHosts, checkMode, ansibleTags) {
    dir("ansible/${env.REPO_NAME}") {
        withEnv(["SSH_KEY=${SSH_KEY_NAME}" ]) {
            sh """
            echo '${extraVars}' > extra_vars.json
            """
            def playbookCmd = "ansible-playbook -i ${params.INVENTORY_FILE} ${params.PLAYBOOK_FILE} -e @extra_vars.json "
            if (checkMode) {
                playbookCmd += " -C"
            }
            if (targetHosts) {
                playbookCmd += " --limit ${targetHosts}"
            }
            if (ansibleTags) {
                playbookCmd += " --tags ${ansibleTags}"
            }
            sh playbookCmd
        }
    }
}

def runAnsiblePlaybookListHosts(extraVars, targetHosts, checkMode, ansibleTags) {
    dir("ansible/${env.REPO_NAME}") {
        withEnv(["SSH_KEY=${SSH_KEY_NAME}" ]) {
            sh """
            echo '${extraVars}' > extra_vars.json
            """
            def playbookCmd = "ansible-playbook -i ${params.INVENTORY_FILE} ${params.PLAYBOOK_FILE} --list-hosts"
            sh playbookCmd
        }
    }
}

def updateInventory() {
    dir("ansible/${env.REPO_NAME}/") {
        script {
            sh """
            if [[ "${env.REGION}" == "west1" ]]; then
                sed -i '/europe-west3/d' "${params.INVENTORY_FILE}"
                sed -i 's/regions:/zones:/g' "${params.INVENTORY_FILE}"
                sed -i 's/  - europe-west1/  - europe-west1-b\\n  - europe-west1-c\\n  - europe-west1-d/g' "${params.INVENTORY_FILE}"
            elif [[ "${env.REGION}" == "west3" ]]; then
                sed -i '/europe-west1/d' "${params.INVENTORY_FILE}"
                sed -i 's/regions:/zones:/g' "${params.INVENTORY_FILE}"
                sed -i 's/  - europe-west3/  - europe-west3-a\\n  - europe-west3-b\\n  - europe-west3-c/g' "${params.INVENTORY_FILE}"
            elif [[ "${env.REGION}" == "both" ]]; then
                sed -i '/europe-west3/d' "${params.INVENTORY_FILE}"
                sed -i '/europe-west1/d' "${params.INVENTORY_FILE}"
                sed -i 's/regions:/zones:/g' "${params.INVENTORY_FILE}"
                sed -i 's/  - europe-west1/  - europe-west1-b\\n  - europe-west1-c\\n  - europe-west1-d/g' "${params.INVENTORY_FILE}"
                sed -i 's/  - europe-west3/  - europe-west3-a\\n  - europe-west3-b\\n  - europe-west3-c/g' "${params.INVENTORY_FILE}"
            fi
            """
        }
    }
}

def updatePlaybook() {
    dir("ansible/${env.REPO_NAME}") {
        script {
            def setupTypeRegex = 'setup_type: ".*"'

            if (fileExists(params.PLAYBOOK_FILE)) {
                def playbookContent = readFile(params.PLAYBOOK_FILE)

                if (playbookContent =~ setupTypeRegex) {
                    sh """
                    if [[ "${env.SETUP_TYPE}" == "install" || "${env.SETUP_TYPE}" == "upgrade" ]]; then
                        sed -i 's/setup_type: ".*"/setup_type: "${env.SETUP_TYPE}"/g' ${params.PLAYBOOK_FILE}
                    else
                        echo "Unknown setup type. Please select either 'install' or 'upgrade'."
                        exit 1
                    fi
                    """
                } else {
                    echo "Playbook doesn't contain the 'setup_type' variable. Skipping update."
                }
            } else {
                echo "Playbook file not found. Skipping update."
            }
        }
    }
}

def getSSHCredentialsId() {
    return 'gpec-svcs-ansible-ssh-key'
}