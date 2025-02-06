pipeline {
  agent any

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '100', artifactNumToKeepStr: '100'))
  }

  parameters {
    string(name: 'index', defaultValue: 'gpec-rpod-*', description: 'ElasticSearch Index/Index Prefix (ex: gpec-rpod-*))')
    string(name: 'timefield', defaultValue: '@timestamp', description: 'Field name to use for start and end date query')
    string(name: 'startDate', defaultValue: 'now-1d/d', description: 'Start date for documents to include, (ex: now-1d/d, YYYY-MM-DDThh:mm:ss)')
    string(name: 'endDate', defaultValue: 'now', description: 'End date for documents to include, (ex: now-1d, YYYY-MM-DDThh:mm:ss)')
    string(name: 'query', defaultValue: '', description: 'Lucene Query like in Kibana search input')
    string(name: 'fields', defaultValue: '', description: 'Fields to include in export as comma separated list')
  }

  environment {
    VAULT_ENGINE_VERSION = vaultHelper.getVaultEngineVersion()

    PROJECT_ARTIFACTID = 'elasticsearch-export'
    PROJECT_GROUPID = 'com.globalpay'
    PROJECT_ARTIFACT_REPO = 'releases'

    ELASTICSEARCH_URL = getElasticSearchHost()
    ELASTICSEARCH_ENV = getElasticSearchEnvironment()
    ELASTICSEARCH_APIKEY = vault path: "secret/pipeline/gcp-gpec-${ELASTICSEARCH_ENV}-elastic-jenkins", key: 'value', engineVersion: VAULT_ENGINE_VERSION

    ARTIFACTORY_URL = 'https://artifactory.rxpdev.com'
  }

  stages {
    stage('Download latest version of the script') {
      steps {
        ansiColor('xterm') {
          script {
            sh '''
                echo "[INFO] "
                echo "[INFO] - Get Latest Version of Artifact - ${PROJECT_ARTIFACTID} from ${PROJECT_ARTIFACT_REPO}"
                echo "[INFO] "
                SEARCH_URL="latestVersion?repos=${PROJECT_ARTIFACT_REPO}&g=${PROJECT_GROUPID}&a=${PROJECT_ARTIFACTID}"

                PROJECT_ARTIFACT_VER=$(curl --silent --insecure https://artifactory.rxpdev.com/artifactory/api/search/${SEARCH_URL})
                echo "[INFO] - Version of ${PROJECT_ARTIFACTID}=${PROJECT_ARTIFACT_VER}"

                echo "[INFO] - Download Artifact"
                curl --silent --insecure "https://artifactory.rxpdev.com/artifactory/${PROJECT_ARTIFACT_REPO}/${PROJECT_GROUPID//.//}/${PROJECT_ARTIFACTID}/${PROJECT_ARTIFACT_VER}/${PROJECT_ARTIFACTID}-${PROJECT_ARTIFACT_VER}.tar.gz" -o ${PROJECT_ARTIFACTID}-${PROJECT_ARTIFACT_VER}.tar.gz

                echo "[INFO] - Extract Artifact"
                tar -zxvf ${PROJECT_ARTIFACTID}-${PROJECT_ARTIFACT_VER}.tar.gz
            '''
          }
        }
      }
    }

    stage("Export query to csv") {
      steps {
        ansiColor('xterm') {
          script {
            sh '''
              set +x
              echo "[WARNING] Jenkins Timezone is 1 hour behind Kibana"
              echo "[INFO] "
              echo "[INFO] - Run query export"
              echo "[INFO] "
              java -Dlogging.config=config/logback.xml -Dspring.config.location=classpath:/application.properties,config/application.properties -jar elasticsearch-export.jar -i "${index}" -s "${startDate}" -e "${endDate}" -q "${query//\\\\/\\/}" -c "${fields}" -t "${timefield}"
            '''
          }
        }
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: 'output.csv', fingerprint: true
      deleteDir()
    }
  }
}

def getElasticSearchHost() {
    switch (env.JENKINS_URL) {
      case ~/.*jenkins\.dev.*/:
        return es.getClusterUrl ([
            clusterName: 'gcp-gpec-dev-elastic',
            urlType: 'pscUrl'
        ])
      case ~/.*jenkins\.cert-ncde.*/:
      case ~/.*jenkins\.prod-ncde.*/:
      case ~/.*jenkins\.staging.*/:
        return es.getClusterUrl ([
            clusterName: 'gcp-gpec-ncde-elastic',
            urlType: 'pscUrl'
        ])
      case ~/.*jenkins\.cert-cde.*/:
      case ~/.*jenkins\.prod-cde.*/:
        return es.getClusterUrl ([
            clusterName: 'gcp-gpec-cde-elastic',
            urlType: 'pscUrl'
        ])
      default:
        throw new Exception("FAILURE: Failed to determine elasticsearch host")
    }
}

def getElasticSearchEnvironment() {
    switch (env.JENKINS_URL) {
      case ~/.*jenkins\.dev.*/:
        return "dev"
      case ~/.*jenkins\.cert-ncde.*/:
      case ~/.*jenkins\.prod-ncde.*/:
      case ~/.*jenkins\.staging.*/:
        return "ncde"
      case ~/.*jenkins\.cert-cde.*/:
      case ~/.*jenkins\.prod-cde.*/:
        return "cde"
      default:
        throw new Exception("FAILURE: Failed to determine elasticsearch environment")
    }
}