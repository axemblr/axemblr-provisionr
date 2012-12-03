#!/bin/bash
mvn -DskipTests -P\!with-integration-tests clean install && \
 (cd providers/$1-tests && mvn -Pwith-live-tests clean install)

