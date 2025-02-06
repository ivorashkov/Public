pipeline {
    agent any
    options {
        timestamps()
        logstash()
    }
    environment {
        ENVIRONMENT_VALUE = getJenkinsEnvironmentString()
        REGION_VALUE = getRegionString()
    }
    stages {
        stage('Generate Choices'){
            steps{
                script{
                    //Generate Dynamic choices based on the current environment
                    def dynamicChoices = generateApplicationChoices()

                    //Define Params with the generated choices
                    properties([
                        parameters([
                            choice(
                                choices: dynamicChoices,
                                description: 'Choose application namespace',
                                name: 'namespace'
                            )
                        ])
                    ])
                }
            }
        }
        stage('Display namespace') {
            steps {
                sh('echo [display namespace]:') // using bash to echo a descriptive message
                echo "${params.namespace}" // display message using jenkins to echo
            }
        }
        stage('Get Pods') {
            steps {
                sh('echo Listing pods from namespace')
                sh('kubectl -n ${namespace} get pods') // using bash to echo a descriptive message
            }
        }
        stage('Select Pod and Execute Health checks') {
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
                    inputPod = userInput ?: ''

                    // Echo to console
                    echo("Pod name: ${inputPod}")
                    sh("kubectl -n ${namespace} exec -it ${inputPod} -- wget -q -O- localhost:8080/actuator/health")
                }
            }
        }
        stage('List Pods after checking') {
            steps {
                echo("Display version for pods from ${namespace} ")
                sh('kubectl -n ${namespace} describe pods |grep -E "Namespace|ISTIO_META_APP_CONTAINERS"')
            }
        }
        stage('Get os-release information') {
            steps {
                script {
                    echo '**************************************** '
                    echo 'GET PODS LABEL <APP> FOR THE NEXT OPERATION '
                    //Get label information and filter the value after app=*, STOPS after separator Comma (,)
                    def appName = sh(script: """
                        kubectl get pods -n ${namespace} --show-labels | sed -n 's/.*app=\\([^,]*\\),.*/\\1/p' | head -n 1
                    """, returnStdout: true).trim()

                    //Print the value
                    echo '**************************************** '
                    echo "EXTRACTED VALUE => app=${appName}"

                    echo '**************************************** '
                    echo 'EXECUTE OS EXRACTION ON ALL PODS'
                    //Connect to pods and Prints out the OS version under PRETTY_NAME variable
                    def output = sh(script: """
                        kubectl -n ${namespace} get pods -l app=${appName} -o name | xargs -I{} kubectl -n ${namespace} exec {} -- cat /etc/os-release | grep PRETTY_NAME | sed 's/"\\(.*\\)"/\\1(Container)/'
                    """, returnStdout: true).trim()

                    echo '******************** OS Version Per All Nodes ******************** '
                    echo "${output}"

                    //Jenkins URL ===> https://deployment-jenkins.${ENVIRONMENT_VALUE}.k8s.euw1.gpecom.io/
                    echo "Jenkins URL ===> ${env.JENKINS_URL}"
                    echo "ENVIRONMENT_VALUE ==> ${ENVIRONMENT_VALUE}"
                    echo "REGION_VALUE ==> ${REGION_VALUE}"

                }
            }
        }
    }

}

def generateApplicationChoices(){
    def appChoices = ["3ds-service-${ENVIRONMENT_VALUE}",
                          "acs-simulator-mb",
                          "acs-simulator-${ENVIRONMENT_VALUE}",
                          "aib-file-mgmt-${ENVIRONMENT_VALUE}",
                          "aib-iso-gateway-${ENVIRONMENT_VALUE}",
                          "aif-aib-gateway-${ENVIRONMENT_VALUE}",
                          "aif-bank-service-${ENVIRONMENT_VALUE}",
                          "aif-gpc-gateway-${ENVIRONMENT_VALUE}",
                          "aif-redsys-gateway-${ENVIRONMENT_VALUE}",
                          "aif-uatp-gateway-${ENVIRONMENT_VALUE}",
                          "amexcpn-gateway-${ENVIRONMENT_VALUE}",
                          "apacs-gateway-${ENVIRONMENT_VALUE}",
                          "apm-service-${ENVIRONMENT_VALUE}",
                          "argo-rollouts",
                          "asm-system",
                          "authorisation-service-${ENVIRONMENT_VALUE}",
                          "auto-account-updater-${ENVIRONMENT_VALUE}",
                          "card-id-service-${ENVIRONMENT_VALUE}",
                          "card-storage-service-${ENVIRONMENT_VALUE}",
                          "cert-manager",
                          "consul",
                          "content-delivery-${ENVIRONMENT_VALUE}",
                          "cortex-ips-${ENVIRONMENT_VALUE}",
                          "crypto-${ENVIRONMENT_VALUE}",
                          "dcc-${ENVIRONMENT_VALUE}",
                          "default",
                          "deployment-jenkins",
                          "enrollment-3ds-${ENVIRONMENT_VALUE}",
                          "escrow-service-${ENVIRONMENT_VALUE}",
                          "external-dns",
                          "fexco-dcc-gateway-${ENVIRONMENT_VALUE}",
                          "filebeat",
                          "fraud-engine-${ENVIRONMENT_VALUE}",
                          "gatecnx-gateway-${ENVIRONMENT_VALUE}",
                          "gatekeeper-system",
                          "hosted-redirect-router-${ENVIRONMENT_VALUE}",
                          "hpa-testing",
                          "hpp-template-uploader-${ENVIRONMENT_VALUE}",
                          "hpp-web-${ENVIRONMENT_VALUE}",
                          "installments-service-${ENVIRONMENT_VALUE}",
                          "istio-egressgateway",
                          "istio-ingressgateway",
                          "istio-system",
                          "kube-node-lease",
                          "kube-public",
                          "kube-system",
                          "logicmonitor",
                          "maas-${ENVIRONMENT_VALUE}",
                          "memcached",
                          "merchant-configuration-service-${ENVIRONMENT_VALUE}",
                          "migs-gateway-${ENVIRONMENT_VALUE}",
                          "ob-file-mgmt-${ENVIRONMENT_VALUE}",
                          "omnipay-gateway-${ENVIRONMENT_VALUE}",
                          "open-banking-${ENVIRONMENT_VALUE}",
                          "otp-gateway-${ENVIRONMENT_VALUE}",
                          "planet-iso-gw-${ENVIRONMENT_VALUE}",
                          "pres-responder",
                          "propay-gateway-${ENVIRONMENT_VALUE}",
                          "rabbitmq",
                          "rc-ui-${ENVIRONMENT_VALUE}",
                          "rxp-api-${ENVIRONMENT_VALUE}",
                          "sso-service-${ENVIRONMENT_VALUE}",
                          "test-archetype-app-${ENVIRONMENT_VALUE}",
                          "tmt-${ENVIRONMENT_VALUE}",
                          "transaction-router-${ENVIRONMENT_VALUE}",
                          "twistlock",
                          "vault",
                          "wds-${ENVIRONMENT_VALUE}",
                          "white-e-${ENVIRONMENT_VALUE}"]
    return appChoices   
}

def getJenkinsEnvironmentString(){
    switch (env.JENKINS_URL) {
      case ~/.*jenkins\.dev.*/:
        return "dev"
      case ~/.*jenkins\.cert-ncde.*/:
        return "cert-ncde"
      case ~/.*jenkins\.staging.*/:
        return "staging"
      case ~/.*jenkins\.cert-cde.*/:
        return "cert-cde"
      case ~/.*jenkins\.prod-cde.*/:
        return "prod-cde"
      default:
        throw new Exception("FAILURE: Failed to determine environment")
    }
}

def getRegionString(){
    switch (env.JENKINS_URL) {
      case ~/.*euw1.*/:
        return "we1"
      case ~/.*euw3.*/:
        return "we3"
      default:
        throw new Exception("FAILURE: Failed to determine region")
    }
}