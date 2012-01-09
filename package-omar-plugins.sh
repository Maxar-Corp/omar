#!/bin/sh

# Cleanup old
rm -rf ~/.ivy2/cache
rm -rf ~/.grails
rm -rf ~/.m2/repository

# Setup ivy cache
cd ~
mkdir -p ~/.ivy2/cache
cd ~/.ivy2/cache
tar xvfz $OMAR_HOME/plugins/ivyCache.tgz

# Setup maven cache
cd $OMAR_HOME/plugins
tar xvfz m2Repository.tgz
cd ~ 
mkdir -p .m2/repository
cd .m2/repository
cp -R $OMAR_HOME/plugins/m2Repository/* . 

# Package OMAR plugins
cd $OMAR_DEV_HOME/plugins
for x in geoscript postgis openlayers omar-oms omar-core omar-security-spring omar-ogc omar-stager omar-video omar-raster omar-superoverlay omar-rss; do
	cd $x
	grails clean
	grails package-plugin 
	cd ..
done

# install the mail plugin into OMAR
cd $OMAR_HOME
#grails install-plugin plugins/grails-mail-1.0-SNAPSHOT.zip
