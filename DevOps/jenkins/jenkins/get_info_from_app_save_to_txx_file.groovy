pipeline {
	agent any
    options {
        timestamps()
        logstash()
        buildDiscarder logRotator(daysToKeepStr: '20', numToKeepStr: '20')
        disableConcurrentBuilds()
    }

    parameters {
         choice (choices: [
            'choice',
            'choice'
         ], description: 'Choose application namespace', name: 'namespace')
    }

    stages {

        stage('Display namespace') {
            steps {
                sh('echo [display namespace]:') // using bash to echo a descriptive message
                echo "${params.namespace}" // display message using jenkins to echo
            }
        }

        stage('Get Pods') {
            steps {
                sh ('echo Listing pods from namespace' )
                sh('kubectl -n ${namespace} get pods') // using bash to echo a descriptive message
            }
        }
        stage("Select Pod and Execute Health checks") {
            options {
				timeout(time: 5, unit: 'MINUTES')
			}
            steps {
                script {

                    // Variables for input
                    def inputPod

                    // Get the input
                    def userInput = input(
                            id: 'userInput', message: 'Enter pod name:?',
                            parameters: [

                                    string(defaultValue: '',
                                            description: 'Insert the pod name from the pipeline',
                                            name: 'Pod'),
                            ])

                    // Save to variables. Default to empty string if not found.
                    inputPod = userInput?:''

                    // Echo to console
                    echo("Pod name: ${inputPod}")
                    sh("kubectl -n ${namespace} exec -it ${inputPod} -- wget -q -O- localhost:8080/actuator/health")

                    // Saving the output to file
                    echo("Exporting output to file")
                    def jobOutput = sh(script: "kubectl -n ${namespace} exec -it ${inputPod} -- wget -q -O- localhost:8080/actuator/health", returnStdout: true).trim()

                    writeFile file: 'job_output.txt', text: jobOutput
                    archiveArtifacts artifacts: 'job_output.txt', fingerprint: true
                }
            }
        }

        stage('Display version for pods') {
            steps {
                echo("Display version for pods from ${namespace} ")
                sh('kubectl -n ${namespace} describe pods |grep -E "Namespace|ISTIO_META_APP_CONTAINERS"') 
            }
        }

        stage('Export output to file') {    
            steps {
                script {
                    echo("Exporting output to file")
                    def job2Output = sh(script: 'kubectl -n ${namespace} describe pods |grep -E "Namespace|ISTIO_META_APP_CONTAINERS" ', returnStdout: true).trim()

                    writeFile file: 'ns_output.txt', text: job2Output
                    archiveArtifacts artifacts: 'ns_output.txt', fingerprint: true
                }
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