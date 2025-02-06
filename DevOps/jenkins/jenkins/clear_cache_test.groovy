pipeline{

    agent any

    environment{
       
        ENVIRONMENT_VALUE = getJenkinsEnvironmentString()
        REGION_VALUE = getRegionString()
        NAMESPACE_VALUE = getNamespace("${ENVIRONMENT_VALUE}")
       
    }

    stages{

        stage('Testing'){
            steps{
                script{
                    echo "ENVIRONMENT_VALUE => ${ENVIRONMENT_VALUE}"
                    echo "REGION_VALUE => ${REGION_VALUE}"
                    echo"NAMESPACE_VALUE => ${NAMESPACE_VALUE}"
                }
            }
        }

        stage('Choose Option') {
            steps {
                script {
                    def dynamicChoices = sh(script: """ kubectl -n ${NAMESPACE_VALUE} get pods -o jsonpath=\'{.items[*].metadata.name}\' """, returnStdout: true).trim().split(/\s+/)
                    echo "${dynamicChoices}" 

                    def userInput = input(
                        id: 'userInput',
                        message: 'Select an option:',
                        parameters: [
                            [$class: 'ChoiceParameterDefinition', 
                             name: 'option', 
                             choices: dynamicChoices.join('\n'), 
                             description: 'Choose an option']
                        ]
                    )

                    echo "Selected option: ${userInput}"
                    
                    //clear crypto cache
                    sh("kubectl -n ${NAMESPACE_VALUE} exec -it ${userInput} -- wget -q -O- {endpoint}")

                    //check healthcheck
                    sh("kubectl -n ${NAMESPACE_VALUE} exec -it ${userInput} -- wget -q -O- {endpoint}")
                }
            }
        }

    }
}

def getNamespace(ENVIRONMENT_VALUE){
    return "crypto-${ENVIRONMENT_VALUE}"
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