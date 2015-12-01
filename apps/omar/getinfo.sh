#!/bin/sh
export LD_LIBRARY_PATH=$OSSIM_INSTALL_PREFIX/lib:$LD_LIBRARY_PATH
export DYLD_LIBRARY_PATH=$OSSIM_INSTALL_PREFIX/lib:$DYLD_LIBRARY_PATH
#---
# File: getinfo.sh
#
# Test script to call swig generated DataInfo.java class and associated
# coms c++ class oms::DataInfo.  Basically if this runs your oms/joms wrappers for
# omar should work. If given a valid image you should get and xml doc to standard
# output with image info.
#
# Calls usually go back and forth like this:
# omar<-->joms<-->oms<-->ossim
#
# Requires: joms.jar
# unix/linux: liboms.so and libjoms.so
# OSX: liboms.dylib and libjoms.dylib (could be in framework on OSX also)
#
# Use java args, -cp and -Djava.library.path as needed.
# -cp is CLASSPATH to joms.jar
# -Djava.library.path points to directory where libjoms.so or .dylib resides.
#
# Typically:
#
# joms.jar is placed in omar/plugins/omar-oms/lib
# libjoms.so or libjoms.dylib is in LD_LIBRARY_PATH or DYLD_LIBRARY_PATH
# 
# Usages:
#
# Both CLASSPATH and (DY)LD_LIBRARY_PATH set:
# 
# java org.ossim.oms.apps.DataInfoTest <image(s)>
#
# No CLASSPATH, (DY)LD_LIBRARY_PATH set:");
#
# java -cp $OMAR_HOME/../../plugins/omar-oms/lib/joms.jar org.ossim.oms.apps.DataInfoTest <image(s)>
#
# No CLASSPATH, No (DY)LD_LIBRARY_PATH set:");
# Note: /path/to/lib should have libjoms.so or libjoms.dylib in it.");
#
# java -Djava.library.path=/path/to/lib -cp $OMAR_HOME/../../plugins/omar-oms/lib/joms.jar org.ossim.oms.apps.DataInfoTest <image(s)>
# 
# ---
java -cp $OSSIM_DEV_HOME/oms/lib/joms-$OSSIM_VERSION.jar org.ossim.oms.apps.DataInfoTest $1
