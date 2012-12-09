#!/bin/bash

if [ -z $1 ]; 
then
    echo "Usage: process_test.sh <provider-id> (e.g. amazon or cloudstack)"
    echo
    echo "Make sure you add your credentials to ~/.m2/settings.xml"
    echo "See README for documentation"
else
    if [ -d "providers/$1-tests" ]; then
        mvn -DskipTests -DskipKarafTests clean install && \
         (cd providers/$1-tests && mvn -Pwith-live-tests clean install)
    else
        echo "Folder not found: providers/$1-tests"
    fi
fi 

