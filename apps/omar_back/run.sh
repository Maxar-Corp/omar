#!/bin/bash
export JAVA_OPTS="-Xmx1024m -Xms256m -Djava.awt.headless=true"
#export JAVA_OPTS="-Xmx1024m -Xms256m -Xmn512m -ParallelGCThreads=4 -XX:+UseParallelOldGC -Djava.awt.headless=true"
#export JAVA_OPTS="-server -Xcomp -Xbatch -Xss1m -Xms256m -Xmx1024m -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:NewSize=128m -XX:MaxPermSize=128M -XX:CMSInitiatingOccupancyFraction=80" 

#nohup grails prod run-war &
#grails prod run-app
#grails prod run-war
#grails run-war
grails run-app

