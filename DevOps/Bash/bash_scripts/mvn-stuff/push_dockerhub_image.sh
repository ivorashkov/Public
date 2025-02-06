#!/bin/bash

echo "********************"
echo "** Pushing image ***"
echo "********************"

IMAGE="maven-project"

echo "** Logging in ***"
docker login -u ivaylorashkov -p $PASS
echo "*** Tagging image ***"
docker tag $IMAGE:$BUILD_TAG ivaylorashkov/$IMAGE:$BUILD_TAG
echo "*** Pushing image ***"
docker push ivaylorashkov/$IMAGE:$BUILD_TAG