#!/bin/sh

read -p "Path to source jar: "  PATH_TO_JAR
read -p "Group ID: "  GROUP_ID
read -p "Artefact ID: " ARTEFACT_ID
read -p "Version: " VERSION


#PATH_TO_JAR=$OMAR_HOME/plugins/spring-test-3.0.5.RELEASE.jar
#GROUP_ID=org.springframework
#ARTEFACT_ID=spring-test
#VERSION=3.0.5.RELEASE
PACKAGING=jar

mvn install:install-file -Dfile=$PATH_TO_JAR -DgroupId=$GROUP_ID \
    -DartifactId=$ARTEFACT_ID -Dversion=$VERSION -Dpackaging=$PACKAGING


