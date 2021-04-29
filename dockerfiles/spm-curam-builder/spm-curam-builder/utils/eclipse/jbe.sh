#!/bin/sh
#*******************************************************************************
# Licensed Materials - Property of IBM
# (c) Copyright IBM Corporation 2010, 2012. All Rights Reserved.
#
# Note to U.S. Government Users Restricted Rights:
# Use, duplication or disclosure restricted by GSA ADP Schedule
# Contract with IBM Corp.
#*******************************************************************************
PRGPATH="`dirname "$0"`"
# We recommend using the JDK included in the RTC eclipse client.
JAVA_EXE=$JAVA_HOME/bin/java
# For Mac OS X (unsupported, but used by developers), use the default JRE
if [ `uname` = Darwin ]; then JAVA_EXE=java; fi
LAUNCHER_JAR="${PRGPATH}/plugins/org.eclipse.equinox.launcher_1.1.1.R36x_v20101122_1400.jar"
exec "${JAVA_EXE}" -Djava.protocol.handler.pkgs=com.ibm.net.ssl.www2.protocol -Xmx300m -Dosgi.requiredJavaVersion=1.6 -jar "${LAUNCHER_JAR}" "$@"
