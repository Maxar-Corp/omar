#!/bin/sh

# Cleanup old
#rm -rf ~/.grails/ivy-cache
#rm -rf ~/.m2/repository

# Setup ivy cache
#mkdir -p ~/.grails
#cd ~/.grails
#tar xvfz $OMAR_HOME/plugins/ivyCache.tgz

# Setup maven cache
#mkdir -p ~/.m2
#cd ~/.m2
#tar xvfz $OMAR_HOME/plugins/m2Repository.tgz

# Package OMAR plugins
cd $OMAR_DEV_HOME/plugins
for x in postgis geoscript openlayers omar-oms  omar-opensearch omar-common-ui omar-security-spring omar-core omar-ogc omar-stager omar-video omar-raster omar-chipper omar-superoverlay omar-image-magick omar-rss omar-federation; do
	cd $x
#	grails -non-interactive clean
#	grails -non-interactive upgrade
	grails -non-interactive package-plugin 
	cd ..
done

# plugin.xml files will be regenerated when creating the war
# rm `find . -name plugin.xml`
# the removing seems to still not work.  We will just retouch the postgis plugin and see if
# that will help
#cd $OMAR_DEV_HOME/plugins/postgis
#grails package-plugin
cd $OMAR_DEV_HOME

