pipeline {
    agent any
    options {
        timestamps()
        ansiColor('xterm')
        buildDiscarder logRotator(daysToKeepStr: '20', numToKeepStr: '20')
        disableConcurrentBuilds()
    }

    stages {
        stage('Starting Healthcheck Testing in GCP-W3') {
            steps {
                sh '''#!/bin/bash
                echo "Checking hostname \033[0;32m $HOSTNAME \033[0m for load and uptime"
                uptime
                sleep 5
                '''
            }
        }
        stage('Healthcheck Testing for Gateways in GCP-W3'){
            steps {
                sh '''#!/bin/bash
                    GREEN="\033[1;32m"
                    RED="\033[1;31m"
                    Yellow="\033[1;33m"
                    NC="\033[0m"

                       hosts=(URL
                       URL
                       URL)
                       
                    echo -e $"\n '$Yellow' Healthcheck Testing for Gateways in GCP-W3 '$NC'"
                    for i in "${hosts[@]}"
                        do
                        if curl -v -k --silent --max-time 4 $i 2>&1 | grep status
                        then
                        echo -e "Host $i: '$GREEN' OK - Running '$NC'"
                        sleep 2
                        else
                        echo -e "Host $i: '$RED' FAILED '$NC'"
                        fi
                        done
                    '''
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