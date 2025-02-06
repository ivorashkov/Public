#!/bin/bash

crumb=$(curl -u "username:password" -s 'http://{jenkins-url}/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,":",//crumb)')

curl -u "username:password" -H "$crumb" -X POST http://{URL taken from Build Now Button}
curl -u "username:password" -H "$crumb" -X POST http://{URL}/buildWithParameters?MYSQL_HOST=db_host&DATABASE_NAME=testdb&AWS_BUCKET_NAME=jenkins-mysql-backup
----------
crumb=$(curl -u "jenkins:1234" -s 'http://jenkins.local:8080/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,":",//crumb)')
# curl -u "jenkins:1234" -H "$crumb" -X POST http://jenkins.local:8080/job/ENV/build?delay=0sec
curl -u "jenkins:1234" -H "$crumb" -X POST  http://jenkins.local:8080/job/backup-to-aws/buildWithParameters?MYSQL_HOST=db_host&DATABASE_NAME=testdb&AWS_BUCKET_NAME=jenkins-mysql-backup
--------------------