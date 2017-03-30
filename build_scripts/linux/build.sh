#!/bin/bash
pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
pushd $SCRIPT_DIR/../.. >/dev/null
export OMAR_DEV_HOME=$PWD
export OMAR_HOME=$OMAR_DEV_HOME/apps/omar
popd >/dev/null
#
#if [ -z "$GRAILS_VERSION" ]; then
#   echo "******OMAR BUILD ERROR: Environment variable GRAILS_VERSION must be set"
#   exit 1
#fi
#if [ -z "$OSSIM_VERSION" ]; then
#   echo "******OMAR BUILD ERROR: Environment variable GRAILS_VERSION must be set"
#   exit 1
#fi

#source "$HOME/.sdkman/bin/sdkman-init.sh"
#sdk use grails $GRAILS_VERSION
export OSSIM_MAVEN_PROXY="https://artifacts.radiantbluecloud.com/artifactory/ossim-deps"

pushd $OMAR_HOME >/dev/null

#grails prod war omar-${OSSIM_VERSION}.war
./grailsw prod war omar-${OSSIM_VERSION}.war

RETURN_CODE=$?
if [ $RETURN_CODE -ne 0 ];then
    echo "BUILD ERROR: omar war failed build..."
else
    RETURN_CODE=0;
fi

#
popd >/dev/null


exit $RETURN_CODE