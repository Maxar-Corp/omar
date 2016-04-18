#!/bin/bash
pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
pushd $SCRIPT_DIR/../../.. >/dev/null
export ROOT_DIR=$PWD
export OMAR_DEV_HOME=$ROOT_DIR/omar
export OMAR_HOME=$OMAR_DEV_HOME/apps/omar

if [ -z $INSTALL_PREFIX ]; then
   export INSTALL_PREFIX=$OSSIM_DEV_HOME/omar-install
fi

if [ -z $OSSIM_VERSION ]; then
   echo "ERROR: OSSIM_VERSION is not defined.  Please define the OSSIM_VERSION for WAR install"
fi

popd >/dev/null

pushd $OMAR_HOME >/dev/null

install -p -m644 -D omar-${OSSIM_VERSION}.war ${INSTALL_PREFIX}/share/omar/omar-${OSSIM_VERSION}.war
install -p -m644 -D $OMAR_DEV_HOME/support/omarConfig.groovy ${INSTALL_PREFIX}/share/omar/omarConfig-template.groovy
install -p -m644 -D $OMAR_DEV_HOME/support/sql/alter_timezones.sql ${INSTALL_PREFIX}/share/omar/sql/alter_timezones.sql
install -p -m644 -D $OMAR_DEV_HOME/support/sql/alter_to_multipolygon.sql ${INSTALL_PREFIX}/share/omar/sql/alter_to_multipolygon.sql
install -p -m644 -D $OMAR_DEV_HOME/support/sql/config_settings.sql ${INSTALL_PREFIX}/share/omar/sql/config_settings.sql
install -p -m644 -D $OMAR_DEV_HOME/support/sql/determine_autovacuum.sql ${INSTALL_PREFIX}/share/omar/sql/determine_autovacuum.sql
install -p -m644 -D $OMAR_DEV_HOME/support/sql/largest_database.sql ${INSTALL_PREFIX}/share/omar/sql/largest_database.sql
install -p -m644 -D $OMAR_DEV_HOME/support/sql/README.txt ${INSTALL_PREFIX}/share/omar/sql/README.txt

popd >/dev/null

pushd ${INSTALL_PREFIX}/share/omar

if [ ! -f omar.war ]; then
   ln -s omar-${OSSIM_VERSION}.war omar.war
fi

popd

RETURN_CODE=$?
if [ $RETURN_CODE -ne 0 ];then
    echo "BUILD ERROR: omar war failed build..."
else
    RETURN_CODE=0;
fi

#


exit $RETURN_CODE
