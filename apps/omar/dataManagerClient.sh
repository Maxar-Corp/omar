#!/bin/sh 

OMAR_INSTANCE="http://localhost:8080/omar-2.0"
BASEDIR="$HOME/projects/data"
PATTERN="*.jpg"

#for x in `find $BASEDIR -name "$PATTERN"`; do
	java -cp $HOME/.grails/1.2.1/projects/omar/classes org.ossim.omar.DataManagerClient $OMAR_INSTANCE addRaster $x;
#done
