#!/bin/bash
mvn -DskipTests -P\!with-integration-tests -Pwith-assembly clean install
