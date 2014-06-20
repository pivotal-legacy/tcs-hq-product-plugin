#!/bin/sh
# --------------------------------------------------------------------------- 
# tc Server Provisioning Script 
# 
# Copyright (c) 2010 VMware, Inc.  All rights reserved.
# --------------------------------------------------------------------------- 
# version: 6.0.25.A-RELEASE
# build date: 20100406141135

#find out the absolute path of the tcserver-instance.sh script
PRG="$0"

while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`/"$link"
    fi
done
# Get standard environment variables
TIPATH=`dirname "$PRG"`
SCRIPTPATH=`cd "$TIPATH" ; pwd`

#Absolute path
TIPATH=`cd "$TIPATH/tijars" ; pwd`

#Add in -d and -n as a default option. If the user specifies on command line, it overrides
java -cp $TIPATH/commons-cli.jar:$TIPATH/groovy-all.jar:$TIPATH/tcruntime-instance.jar tcruntime_instance "$@" -d $SCRIPTPATH -n $SCRIPTPATH 
