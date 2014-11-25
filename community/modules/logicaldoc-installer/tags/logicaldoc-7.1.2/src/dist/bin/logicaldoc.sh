#! /bin/sh
# Copyright (c) 2008 Logical Objects snc
# All rights reserved.
#
### BEGIN INIT INFO
# Provides:       logicaldoc     
# Required-Start: 
# Should-Start:   
# Required-Stop:
# Default-Start:  3 5
# Default-Stop:   0 1 2 6
# Description:    LogicalDOC Document Management System
### END INIT INFO

ulimit -Hn 6000
ulimit -Sn 6000
ulimit -v unlimited

HOME=%{INSTALL_PATH}
export CATALINA_HOME="$HOME/tomcat"
export CATALINA_PID="$HOME/bin/pid"
export JAVA_OPTS="-Xmx900m -XX:MaxPermSize=128m -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true"

case $1 in
start)   if [ -e $CATALINA_PID ]
         then
            kill -9 `tail $CATALINA_PID`
            rm -rf $CATALINA_PID
         fi
         "$CATALINA_HOME/bin/catalina.sh" start
         ;;
restart) "$CATALINA_HOME/bin/catalina.sh" stop -force
         if [ -e $CATALINA_PID ]
         then
            kill -9 `tail $CATALINA_PID`
            rm -rf $CATALINA_PID
         fi
         "$CATALINA_HOME/bin/catalina.sh" start
         ;;
stop)   "$CATALINA_HOME/bin/catalina.sh" stop -force
         if [ -e $CATALINA_PID ]
         then
            kill -9 `tail $CATALINA_PID`
            rm -rf $CATALINA_PID
         fi
         ;;
*) "$CATALINA_HOME/bin/catalina.sh" $1
   ;;
esac