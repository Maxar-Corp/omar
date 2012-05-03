#!/bin/bash
#export JAVA_OPTS="-Xmx1024m -Xms256m -Djava.awt.headless=true"
#export JAVA_OPTS="-Xmx1024m -Xms256m -Djava.awt.headless=true -Dsun.rmi.dgc.client.gcInterval=10000 -Dsun.rmi.dgc.server.gcInterval=10000 -XX:+PrintGCDetails -verbose:gc"
#export JAVA_OPTS="-Xmx1024m -Xms256m -Djava.awt.headless=true -Xcheck:jni -server -verbose:jni"
#export JAVA_OPTS="-Xmx1024m -Xms256m -Xmn512m -ParallelGCThreads=4 -XX:+UseParallelOldGC -Djava.awt.headless=true"
#export JAVA_OPTS="-server -Djava.awt.headless=true -XX:+HeapDumpOnOutOfMemoryError -XX:MaxGCMinorPauseMillis=500 -XX:MaxGCPauseMillis=100 -XX:MaxNewSize=1500m -Xcomp -Xbatch -Xmx3000m -Xms2048m -XX:NewSize=1500m  -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode  -XX:MaxPermSize=512M -XX:CMSInitiatingOccupancyFraction=80 -XX:+CMSClassUnloadingEnabled -XX:+UseGCOverheadLimit" 
export GRAILS_OPTS=${JAVA_OPTS} 
#export JAVA_OPTS="-server -Xms256m -Xmx1024m -Djava.awt.headless=true  -XX:MaxPermSize=256m -XX:+CMSClassUnloadingEnabled -XX:+UseGCOverheadLimit"

export JAVA_OPTS="-server -Xms256m -Xmx1024m -Djava.awt.headless=true -XX:MaxPermSize=256m -XX:+CMSClassUnloadingEnabled -XX:+UseGCOverheadLimit"
export GRAILS_OPTS=$JAVA_OPTS

#nohup grails prod run-war &
#grails prod run-app
#grails prod run-war
#grails run-war
#grails run-app

case "$1" in
dev)
export mode="dev"
;;
test)
export mode="test"
;;
*)
export mode="prod"
;;
esac

case "$2" in
app)
export style="app"
;;
*)
export style="war"
;;
esac

case "$3" in
true)
grails  ${mode} run-${style}  
;;
*)
nohup grails ${mode} run-${style} & 
echo $! > omar.pid
;;
esac

