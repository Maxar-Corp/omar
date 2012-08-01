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
cd ~ 
mkdir -p .m2/repository
cd .m2/repository
tar xvfz $OMAR_HOME/plugins/m2Repository.tgz

# Package OMAR plugins
cd $OMAR_DEV_HOME/plugins
for x in postgis geoscript openlayers omar-oms  omar-common-ui omar-security-spring omar-core omar-ogc omar-stager omar-video omar-raster omar-superoverlay omar-image-magick omar-rss; do
	cd $x
	grails clean
	grails package-plugin 
	cd ..
done

# plugin.xml files will be regenerated when creating the war
# rm `find . -name plugin.xml`
# the removing seems to still not work.  We will just retouch the postgis plugin and see if
# that will help
cd $OMAR_DEV_HOME/plugins/postgis
grails package-plugin
cd $OMAR_DEV_HOME

