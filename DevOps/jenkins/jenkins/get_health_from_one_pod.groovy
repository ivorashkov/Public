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
            'choice'], description: 'Choose application namespace', name: 'namespace')
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
                stage('Get os-release information'){
            steps{
                script{
                    echo "**************************************** "
                    echo "GET PODS LABEL <APP> FOR THE NEXT OPERATION "
                    //Get label information and filter the value after app=*, STOPS after separator Comma (,)
                    def appName = sh(script: """
                        kubectl get pods -n ${namespace} --show-labels | sed -n 's/.*app=\\([^,]*\\),.*/\\1/p' | head -n 1
                    """, returnStdout: true).trim()
                
                    //Print the value 
                    echo "**************************************** "
                    echo "EXTRACTED VALUE => app=${appName}"


                    echo "**************************************** "
                    echo "EXECUTE OS EXRACTION ON ALL PODS"
                    //Connect to pods and Prints out the OS version under PRETTY_NAME variable
                    def output = sh(script: """
                        kubectl -n ${namespace} get pods -l app=${appName} -o name | xargs -I{} kubectl -n ${namespace} exec {} -- cat /etc/os-release | grep PRETTY_NAME | sed 's/"\\(.*\\)"/\\1(Container)/'
                    """, returnStdout: true).trim()

                    echo "******************** OS Version Per All Nodes ******************** "
                    echo "${output}"

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
