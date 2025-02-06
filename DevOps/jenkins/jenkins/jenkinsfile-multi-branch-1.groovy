CODE_CHANGES = getGitChanges()
def grv
pipeline {

    agent any

    parameters{
        string(name: 'VERSION', defaultValue: '1.0', description: 'Enter the version number:')
        booleanParam(name: 'executeTests', defaultValue: false, description: '')
        choice(name: 'ENVIRONMENT', choices: ['dev', 'test', 'prod'], description: 'Select environment:')
        password(name: 'DB_PASSWORD', defaultValue: '', description: 'Enter the database password:')
        file(name: 'CONFIG_FILE', description: 'Upload configuration file:')
        text(name: 'RELEASE_NOTES', defaultValue: '', description: 'Enter release notes:')
        run(name: 'BUILD_ID', defaultSelector: 'lastSuccessful', description: 'Select a build:')
    }

    tools{
       //access build tools for your projects
       //gradle, maven, yarn, java, js 
       //if we want to use "mvn install" we need to have maven available
       //default available tools are gradle, maven, jdk
       maven 'Maven'//{Name configured in jenkins -> Global Tool configuration}
    }

    environment{
        // if we declare env variables here, they will be available in all stages in this jenkinsfile
        NEW_VERSION = '1.0.0'
        //extract credentials saved in jenkins -> credentials binding plugin
        SERVER_CREDENTIALS = credentials('{credentials_reference_ID}') 
    }

    stages {


        stage('init'){
            steps{
                script{
                    //extracts script from external groovy file to variable
                    grv = load "script.groovy"
                }
            }
        }

        stage('Build') {

            steps {
                script{
                    grv.greet()

                    def buildNumber = currentBuild.number
                    def jobName = env.JOB_NAME
                    def parameters = currentBuild.rawBuild.getAction(ParametersAction.class)?.parameters


                    def result = sh(script: 'ls -l', returnStatus: true)
                    if (result == 0) {
                        echo 'Command executed successfully'
                    } else {
                        error 'Command failed'
                    }
                    
                    // Define a function
                    def greet(name) {
                        echo "Hello, ${name}!"
                    }

                    // Call the function
                    greet('Alice')
                }

                sh "mvn install"
                echo 'building the application...'
                echo "building version ${NEW_VERSION}"
                withCredentials([
                    //USER and PWD are the vars that info would be saved in.
                    usernamePassword(credentials:'credentials_reference_ID',
                    usernameVariable: USER,
                    passwordVariable: PWD)
                ]){
                    sh "some script ${USER} and ${PWD}"
                }
            }
        }
        stage('Test') {

            steps {
                echo 'testing the application...'
                when{
                    expression{
                        //will execute steps for 'Test' stage only if this is true.
                        params.executeTests == true
                    }
                }
            }
        }
        stage('Deploy') {
            when {
                script{
                    grv.deployApp()
                }
                
                branch 'main'  // Execute this stage only for the main branch
                buildingTag()  // Execute only when building a tag
                changeset "**/*.java"  // Execute if changes are detected in Java files
                changeRequest()  // Execute only for change requests
                environment name: 'BUILD_TYPE', value: 'release'  // Execute for release builds
                expression {
                    //Executes the stage or step based on a custom Groovy expression.
                    return params.BUILD_NUMBER > 10
                    BRANCH_NAME == 'dev' || BRANCH_NAME == 'main' && CODE_CHANGES == true
                }
                not {
                    //Negates the condition specified within the when block.
                    branch 'master'  // Execute for branches other than master
                }
                allOf {
                    //Combines multiple conditions using logical AND.
                    branch 'main'
                    buildingTag()
                }
                anyOf {
                    //Combines multiple conditions using logical OR.
                    branch 'main'
                    buildingTag()
                }
            }
            
            steps {
                echo 'deploying the application...'
            }
        }
    }
    // Define post-build actions
    post {
        always {
            // Clean up steps, notifications, etc.
        }
        success {
        // Notify success, publish artifacts, etc.
        }
        failure {
        // Notify failure, send email, etc.
        }
        unstable {
        // Take actions specific to unstable builds
        }
        changed {
        // Perform actions if the build state has changed
        }
        fixed {
        // Actions to take when a failing build is fixed
        }
        aborted {
        // Clean up resources or notify about aborted build
        }
        unstableAlways {
        // Actions to take for unstable builds that still need to be processed
        }
    }
}