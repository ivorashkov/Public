#!/bin/bash

echo "***************************"
echo "** Building jar ***********"
echo "***************************"

WORKSPACE=/home/jenkins/jenkins-data/jenkins_home/workspace/pipeline-docker-maven

#"$@" -> expects params which are not specifically defined.
# in this case -> ./maven.sh mvn -B -DskipTests clean package
docker run --rm  -v  $WORKSPACE/java-app:/app -v /root/.m2/:/root/.m2/ -w /app maven:3-alpine "$@"