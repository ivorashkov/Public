pipeline {
    agent any
    parameters {
        string(name: 'rcName', defaultValue: '', description: 'Release Candidate Name. Leave empty to build all.')
        choice(name: 'vaultEnvironment', choices: ['choice','choice'], description: 'Vault Environment')
    }
    options {
        timeout(time: 850, unit: 'MINUTES')
    }
    stages {
        stage('Prepare') {
            steps {
                script {
                    def projects = [
                        'choice',
                        'choice'
                    ]
                    def concurrentBuilds = 7

                    if (params.rcName.trim() == '') {
                        // Split the projects list into chunks
                        def chunks = projects.collate(concurrentBuilds)

                        // Run each chunk in parallel
                        chunks.eachWithIndex { chunk, index ->
                            def chunkName = "Chunk-${index + 1}"
                            stage(chunkName) {
                                parallel chunk.collectEntries {
                                    ["${it}": { buildProject(it) }]
                                }
                            }
                        }
                    } else {
                        buildProject(params.rcName)
                    }


                }
            }
        }
    }
    post {
        success {
            echo 'Pipeline completed!'
        }
    }
}

def buildProject(String projectName) {
    echo "Triggering build for project: ${projectName} with vaultEnvironment: ${params.vaultEnvironment}"
    
    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        try {
            build(job: 'deploy-secret-from-vault-to-bitbucket', wait: true, parameters: [
                string(name: 'rcName', value: projectName),
                string(name: 'vaultEnvironment', value: params.vaultEnvironment)
            ])
        } catch (Exception e) {
            echo "Error occurred during build for project ${projectName}: ${e.getMessage()}"
        }
    }
}