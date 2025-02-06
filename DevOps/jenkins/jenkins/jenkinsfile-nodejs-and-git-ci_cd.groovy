pipeline {
    agent any

    stages {
        stage('Checkout github repository ') {
            steps {
               //Checkout code froma public github repo
               checkout scm
            }
        }
        stage('NPM install') {
            steps {
                bat 'npm install'
            }
        }
        stage('Run integration tests') {
            steps {
                //using bat for windows instead of sh for linux
                bat 'npm install'
            }
        }
        stage('Build Image') {
            steps {
                withCredentials([usernamePassword(credentialsId:'dasdiashdi-dsadfa-sda'),
                passwordVariable:'DOCKER_PASSWORD',
                usernameVariable: 'DOCKER_USERNAME']){
                    bat """
                        docker build -t dockerHubRepo/imageName:version .
                        docker login -u %DOCKER_USERNAME% --password %DOCKER_PASSWORD%
                        docker push dockerHubRepo/imageName:version
                    """
                }
            }
        }
        stage('Deploy Image') {
            steps {
                script{
                    //prompt for input approval
                    input('Deploy to production?')
                }
                withCredentials([
                    usernamePassword(credentialsId:'dasdiashdi-dsadfa-sda'),
                    passwordVariable:'DOCKER_PASSWORD',
                    usernameVariable: 'DOCKER_USERNAME']){
                        bat """
                            docker pull dockerHubRepo/imageName:version
                            docker run -d --name container_name -p {app_port:container_port} -v {volume_path} dockerHubRepo/imageName:version/tag
                            //docker-compose -f docker-compose.yml up -d
                        """
                }
            }
        }
    }
}
