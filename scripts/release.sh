#!/bin/bash

RELEASE_VERSION=$1
DEVELOPMENT_VERSION=$2

mvn -Pwith-assembly release:clean release:prepare -DreleaseVersion=$RELEASE_VERSION \
    -Dtag=provisionr-$RELEASE_VERSION -DdevelopmentVersion=$DEVELOPMENT_VERSION -DpushChanges=false

mvn -Pwith-assembly clean release:perform -DconnectionUrl=scm:git:file://`pwd`/.git \
    -Dtag=provisionr-$RELEASE_VERSION -Dgoals="package -DskipTests -DskipKarafTests" 

echo "Done. Please upload the artifact and git push && git push --tags"

