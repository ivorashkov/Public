#!/bin/bash

DB_HOST=$1
DB_PASSWORD=$2
DB_NAME=$3
AWS_SECRET=$4
BUCKET_NAME=$5
BACKUP=db-$ATE.sql
DATE=$(date +%m_%d_%H:%M)

mysqldump -u root -h $DB_HOST -p $DB_PASSWORD $DB_NAME > /tmp/$BACKUP && \
export AWS_ACCESS_KEY_ID={value} && \
export AWS_SECRET_ACCESS_KEY=$AWS_SECRET {or extract it from somewhere for security} && \
echo "Uploading your $BACKUP file" && \
aws s3 cp /tmp/$BACKUP s3://$BUCKET_NAME/$BACKUP