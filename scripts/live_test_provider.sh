#!/bin/bash

if [ -z $1 ]; 
then
    echo "Usage: live_test_provider.sh <provider-id> (e.g. amazon or cloudstack)"
    echo
    echo "Make sure you add your credentials to ~/.m2/settings.xml"
    echo "See README for documentation"
else
    mvn -DskipTests -P\!with-integration-tests clean install && \
      (cd providers/$1-tests && mvn -Pwith-live-tests clean install)
fi 
