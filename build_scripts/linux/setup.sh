#!/bin/bash

if [ ! -d "$HOME/.sdkman" ]; then
   curl -s get.sdkman.io | bash
fi
source "$HOME/.sdkman/bin/sdkman-init.sh"

#if [ ! -z "$GROOVY_VERSION" ]; then
#   if [ ! -d "$HOME/.sdkman/groovy/$GROOVY_VERSION" ]; then
#      sdk install groovy $GROOVY_VERSION
#   fi
#else
#   echo "******OMAR SETUP ERROR: Environment variable GROOVY_VERSION must be set"
#   exit 1
#fi

if [ ! -z "$GRAILS_VERSION" ]; then
   if [ ! -d "$HOME/.sdkman/grails/$GRAILS_VERSION" ]; then
      sdk install grails $GRAILS_VERSION
   fi
else
   echo "******OMAR SETUP ERROR: Environment variable GRAILS_VERSION must be set"
   exit 1
fi
export OSSIM_MAVEN_PROXY="https://artifacts.radiantbluecloud.com/artifactory/ossim-deps"

#if [ ! -z "$GRADLE_VERSION" ]; then
#   if [ ! -d "$HOME/.sdkman/gradle/$GRADLE_VERSION" ]; then
#      sdk install gradle $GRADLE_VERSION
#   fi
#else
#   echo "******OMAR SETUP ERROR: Environment variable GRADLE_VERSION must be set"
#   exit 1
#fi
