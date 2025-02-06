pipeline {
	agent any
    options {
        timestamps()
        logstash()
        buildDiscarder logRotator(daysToKeepStr: '20', numToKeepStr: '20')
        disableConcurrentBuilds()
    }

    parameters {
         choice (choices: ['crypto-staging'], description: 'Crypto namespace', name: 'Namespace') // default option must be always
         booleanParam(name: 'ConfirmTiering', defaultValue: false, description: 'This is a TIER 1 JOB') // true or false option
    }

    stages {

        stage('Display Namespace') {
            steps {
                sh('echo [display Namespace]:') // using bash to echo a descriptive message
                echo "${params.Namespace}" // display message using jenkins to echo
            }
        }

        stage('Get Pods') {
            steps {
                sh ('echo Listing pods from Namespace' )
                sh('kubectl -n ${Namespace} get pods') // using bash to echo a descriptive message
            }
        }
        stage("Select Pod and Execute Clear Crypto cache") {
            options {
				timeout(time: 5, unit: 'MINUTES')
			}
            when {
                expression {
                    params.ConfirmTiering == true
                }
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

                    // Execute crypto clear cache
                    echo("Pod name: ${inputPod}")
                    sh("kubectl -n ${Namespace} exec -it ${inputPod} -- wget -q -O- http://127.0.0.1:8080/application/cachemanager.cgi")

                    // Execute health check after clearing the cache
                    echo("Pod name: ${inputPod}")
                    sh("kubectl -n ${Namespace} exec -it ${inputPod} -- wget -q -O- localhost:8080/actuator/health")
                }
            }
        }
        stage('List Pods after checking') {
            steps {
                sh('kubectl -n ${Namespace} get pods')
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

            notifyGoogleChat()
        }
    }
}

def notifyGoogleChat() {
	if (params.mode == 'verify')
		return 
	println "Sending execution to google chat"
	url='gchat-token'
	username = getBuildUser(currentBuild)
	time = sh(returnStdout:true, script: "date +'%d-%m-%y@%T'")
	message = ''
	if (currentBuild.result == 'SUCCESS') {
		message = """Executed sucessfully build:${currentBuild.currentResult}: ${env.JOB_NAME} """
	} else {
		message = """Failed. Run test manually using this procedure https://gpecom.atlassian.net/wiki/spaces/PlatformOps/pages/35878557/Clear+Crypto+cache+procedure """
	}

	postRequestBody = """{
		"text": "CLEAR CACHE JOB: ${message.trim()}
        > Job report: ${env.BUILD_URL}
		> user: ${username}
		> time: ${time}"
	}
	"""

	httpRequest(quiet: true, contentType: 'APPLICATION_JSON', httpMode: 'POST', url: url, requestBody: postRequestBody.replace("\t", " "))
}

def getBuildUser(build) {
	if (build.getBuildCauses()[0]["shortDescription"].startsWith("Started by timer")) {
		return "Timer"
	}

	def triggeredBy = "${build.getBuildCauses('hudson.model.Cause$UserIdCause').userName[0]}"
	if (triggeredBy != "null") {
	  return triggeredBy
	}

	def builds = build.upstreamBuilds
	builds.each { upstreamBuild ->
		triggeredBy = getBuildUser(upstreamBuild)
		if (triggeredBy != "null") {
			return triggeredBy
		}
	}

	return triggeredBy
}