pipeline {
	agent any
    options {
        timestamps()
        logstash()
        buildDiscarder logRotator(daysToKeepStr: '20', numToKeepStr: '20')
        disableConcurrentBuilds()
    }

    parameters {
        choice(name: 'command', choices: ['curl', 'ping'], description: 'Command to use (ex: curl, ping)')
        choice(name: 'parameter', choices: ['-k', '-k -v', '-c 4'], description: 'Parameter to use with the command. Examples: -k for curl, -c 4 for ping.')
        string(name: 'payload', defaultValue: 'localhost', description: 'Payloads to use. Examples: localhost, 127.0.0.1, 127.0.0.1:80.')
    }

    stages {

        stage('Display command + payload') {
            steps {
                sh('echo [display command and payload]:') // using bash to echo a descriptive message
                echo "${params.command} ${params.parameter} ${params.payload}" // display message using jenkins to echo
            }
        }

        stage('Execute command + payload') {
            steps {
                sh('echo [execute command and payload]:') // using bash to echo a descriptive message
                sh('${command} ${parameter} ${payload}') // using bash to execute the command from the strings
            }
        }
    }

    post {
        always{
            echo 'JOB DETAILS'
            echo "BUILD STATUS: ${currentBuild.currentResult}: ${env.JOB_NAME}"
            sh '''#!/bin/bash
            echo -e "EXECUTOR DETAILS"
            uname -a
            cat /etc/*release
            '''
        }
    }
}
