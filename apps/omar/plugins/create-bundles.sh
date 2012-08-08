#!/bin/sh
tmp=$PWD
cd ~/.grails
tar cvfz $OMAR_HOME/plugins/ivyCache.tgz ./ivy-cache
cd ~/.m2
tar cvfz $OMAR_HOME/plugins/m2Repository.tgz ./repository
cd $tmp

