pipeline {
    agent any

    parameters {
        string(name: 'TARGET_HOSTS', description: 'Use for Specific Hosts only | Single or Multiple', defaultValue: '')
        booleanParam(name: 'CHECK_MODE', defaultValue: true, description: 'Enable Ansible Check Mode')
    }

    options {
        timestamps()
        ansiColor('xterm')
    }

    triggers {
        cron('H H * * 0') // This schedule runs the pipeline every week on Sunday
    }

    environment {
        ANSIBLE_FORCE_COLOR = 'true'
        GOOGLE_APPLICATION_CREDENTIALS = credentials('gcp-ansible-key')
        HTTP_PROXY = squid.getSquidUrl()
        HTTPS_PROXY = squid.getSquidUrl()
        http_proxy = squid.getSquidUrl()
        https_proxy = squid.getSquidUrl()
        NO_PROXY = 'rxpdev.com,metadata.google.internal,gpecom.io'
        VAULT_ENGINE_VERSION = vaultHelper.getVaultEngineVersion()
        TWINS_EMAIL = 'gpecom.coresolutions@globalpay.com'
        ALERT_CHAT = 'URL'
    }

    stages {
        stage('Set Lifecycle Parameters') {
            steps {
                script {
                    if (env.JENKINS_URL.contains('dev')) {
                        env.LIFECYCLE = 'dev'
                        env.REGION = 'both'
                        env.PLAYBOOK_FILE = "playbooks/${env.LIFECYCLE}/${env.JOB_BASE_NAME}/playbook.yml"
                    } else if (env.JENKINS_URL.contains('staging')) {
                        env.LIFECYCLE = 'staging'
                        env.REGION = env.JENKINS_URL.contains('euw1') ? 'west1' : 'west3' 
                        env.PLAYBOOK_FILE = "playbooks/${env.LIFECYCLE}/${env.JOB_BASE_NAME}/playbook-${env.REGION}.yml"
                    } else if (env.JENKINS_URL.contains('cert-ncde')) {
                        env.LIFECYCLE = 'cert-ncde'
                        env.REGION = env.JENKINS_URL.contains('euw1') ? 'west1' : 'west3'
                        env.PLAYBOOK_FILE = "playbooks/${env.LIFECYCLE}/${env.JOB_BASE_NAME}/playbook-${env.REGION}.yml"
                    } else if (env.JENKINS_URL.contains('cert-cde')) {
                        env.LIFECYCLE = 'cert-cde'
                        env.REGION = env.JENKINS_URL.contains('euw1') ? 'west1' : 'west3'
                        env.PLAYBOOK_FILE = "playbooks/${env.LIFECYCLE}/${env.JOB_BASE_NAME}/playbook-${env.REGION}.yml"
                    } else if (env.JENKINS_URL.contains('prod-cde')) {
                        env.LIFECYCLE = 'prod-cde'
                        env.REGION = env.JENKINS_URL.contains('euw1') ? 'west1' : 'west3'
                        env.PLAYBOOK_FILE = "playbooks/${env.LIFECYCLE}/${env.JOB_BASE_NAME}/playbook-${env.REGION}.yml"
                    } else if (env.JENKINS_URL.contains('prod-ncde')) {
                        env.LIFECYCLE = 'prod-ncde'
                        env.REGION = 'both'
                        env.PLAYBOOK_FILE = "playbooks/${env.LIFECYCLE}/${env.JOB_BASE_NAME}/playbook.yml"
                    } else if (env.JENKINS_URL.contains('shared')) {
                        env.LIFECYCLE = 'shared'
                        env.REGION = 'both'
                        env.PLAYBOOK_FILE = "playbooks/${env.LIFECYCLE}/${env.JOB_BASE_NAME}/playbook.yml"
                    } else {
                        error('Invalid JENKINS_URL')
                    }
                        env.INVENTORY_FILE="inventories/${env.LIFECYCLE}.gcp.yml"
                        env.SSH_KEY_NAME = "${WORKSPACE}/gpec-svcs-ansible-ssh-key"
                        env.SETUP_TYPE = 'install'
                        env.WIN_USER = vault path: 'secret/pipeline/ANSIBLE_WINRM_USER', key: 'value', engineVersion: VAULT_ENGINE_VERSION
                        env.WIN_PASS = vault path: 'secret/pipeline/ANSIBLE_WINRM_PASSWORD', key: 'value', engineVersion: VAULT_ENGINE_VERSION
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
                        def applicationName = setApplicationName()
                        def REPO_URL = "ssh://git@bitbucket.rxpdev.com:7999/ans/${applicationName}.git"
                        sh "git clone ${REPO_URL}"
                        script {
                            def repoName = getRepoName(REPO_URL)
                            env.REPO_NAME = repoName
                        }
                        echo "The Playbook that will be Installed is : ${env.REPO_NAME} and ${env.JOB_BASE_NAME} at Europe ${env.REGION} with Playbook ${env.PLAYBOOK_FILE} and ${env.INVENTORY_FILE}"
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

        stage('Display Information') {
            steps {
                echo "+-----------------+"
                echo "|  Target Hosts   |"
                echo "+-----------------+"
                runAnsiblePlaybookListHosts(params.TARGET_HOSTS, params.CHECK_MODE)
                echo "+----------------------+-----------------------------------+"
                echo "|       Parameter      |              Value                |"
                echo "+----------------------+-----------------------------------+"
                echo "|   Repo URL           | ${env.REPO_NAME.padRight(33)}|"
                echo "|   Application Name   | ${env.JOB_BASE_NAME.padRight(33)}|"
                echo "|   Inventory File     | ${env.INVENTORY_FILE.padRight(33)}|"
                echo "|   Playbook File      | ${env.PLAYBOOK_FILE.padRight(33)}|"
                echo "|   Setup Type         | ${env.SETUP_TYPE.padRight(33)}|"
                echo "|   Region             | ${env.REGION.padRight(33)}|"
                echo "+----------------------+-----------------------------------+"
            }
        }

        stage('Run Ansible Playbook') {
            steps {
                script {
                    // if the build is triggered manually or by a timer
                 if (currentBuild.getBuildCauses('hudson.triggers.TimerTrigger$TimerTriggerCause').isEmpty()) {                 

                    // prompt for user input only if triggered manually
                    def userInput = input(
                        id: 'confirm',
                        message: 'Apply Playbook?',
                        parameters: [booleanParam(defaultValue: false , description: 'Apply Playbook', name: 'CONFIRM')]
                     )   

                      if (userInput == true) {                   
                          echo "Confirmed successfully"
                          runAnsiblePlaybook(params.TARGET_HOSTS, params.CHECK_MODE) 
                       } 
                       
                      else {
                          echo "Confirmation box was not checked"
                          currentBuild.result = 'FAILURE'
                          return
                      }                
                  } 

                  else {
                       runAnsiblePlaybook(params.TARGET_HOSTS, params.CHECK_MODE)
                    }
                    
                }
                       
            }
        }
    }

    post {
        always {
            echo "Finished:: ${JOB_BASE_NAME}"
            deleteDir()
        }
        success {
            echo "Succeeded:: ${JOB_BASE_NAME}"
        }
        failure {
            echo "Failed:: ${JOB_BASE_NAME}"
            jobFailureAlert(ALERT_CHAT,TWINS_EMAIL)
        }
    }
}

def getRepoName(repoUrl) {
    def folderName = repoUrl.substring(repoUrl.lastIndexOf('/') + 1, repoUrl.lastIndexOf('.git'))
    return folderName
}

def setApplicationName() {
    def jobBaseName = env.JOB_BASE_NAME
    if (jobBaseName.equals('stunnel')) {
        return "gcp-stunnel-playbook"
    }
    else if (jobBaseName.equals('stunnel-gnap'))  {
        return "gcp-stunnel-gnap-playbook"
    }
    else if (jobBaseName.equals('nginx'))  {
        return "gcp-ansible-playbooks"
    }
    else {
        return "gcp-ansible-playbooks"
    }
}

def runAnsiblePlaybook(targetHosts, checkMode) {
    dir("ansible/${env.REPO_NAME}") {
        withEnv(["SSH_KEY=${SSH_KEY_NAME}" ]) {
            def playbookCmd = "ansible-playbook -i ${env.INVENTORY_FILE} ${env.PLAYBOOK_FILE}"
            if (checkMode) {
                playbookCmd += " -C"
            }
            if (targetHosts) {
                playbookCmd += " --limit ${targetHosts}"
            }
            sh playbookCmd
        }
    }
}

def runAnsiblePlaybookListHosts(targetHosts, checkMode) {
    dir("ansible/${env.REPO_NAME}") {
        withEnv(["SSH_KEY=${SSH_KEY_NAME}" ]) {
            def playbookCmd = "ansible-playbook -i ${env.INVENTORY_FILE} ${env.PLAYBOOK_FILE} --list-hosts"
            sh playbookCmd
        }
    }
}

def getSSHCredentialsId() {
    return 'gpec-svcs-ansible-ssh-key'
}

def notifyFailed(email) {
        step([$class: 'Mailer', 
            notifyEveryUnstableBuild: true, 
            recipients: "${email}",
            sendToIndividuals: true])
}

def jobFailureAlert(chatWebhook, email){
    subject = "FAILURE: ${currentBuild.fullDisplayName}"
    message = "${subject}\nJob: ${env.JOB_NAME}, build:${env.BUILD_NUMBER}\nMore info at: ${env.BUILD_URL}"

    try{
        aBody= "{'text': \"${message}\"}"
        resp = httpRequest httpMode: 'POST',
         url: "${env.ALERT_CHAT}",
         requestBody : "${aBody}",
         customHeaders: [ [name: 'Cache-Control', value: "no-cache"], [name: 'Content-type', value: 'application/json']]

    }catch(Exception e){
        println "Issue to alert with google chat, sending alert by email"
        println "Exception: ${e}"
        mail to:"${email}", subject:"${subject}", body: "${message}"
    }
}