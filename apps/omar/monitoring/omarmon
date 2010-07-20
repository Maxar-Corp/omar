#!/bin/bash
# 
#  omarmon.sh
#  Monitors a single instance of OMAR and restarts the process if necessary
#  
#  Created by Jason Moskowitz on 2010-05-17.
#  Copyright 2010 RadiantBlue Technologies Inc., All rights reserved.
# 

# Variables
PID_FILE='/tmp/.omar-pid'
ERROR_FILE='/tmp/.omar-error'
HOSTNAME=`hostname`.`domainname`
MAILTO='root'

# Source omar user environment
if [ -f /home/omar/.bashrc ]; then
	. /home/omar/.bashrc
fi

# Print syntax if no arguments are given
__syntax() {
  echo "Usage: $0 {run}"
  exit 1
}

# Check for OMAR prerequisties
__prereqs() {
if [ -z "$OMAR_HOME" ]
  then
    echo "OMAR_HOME not set"
    exit 1
fi
}

# Get PID of OMAR process
__get_pid() {
   OMAR_PID=`cat $OMAR_HOME/omar.pid`
      echo $OMAR_PID > $PID_FILE    
}

# Check for running OMAR and start new instance if there is a problem
__monitor() {
  cat $PID_FILE |
    while read INSTANCE_PID
      do
        PID_TEST=`ps -f -p $INSTANCE_PID | grep $INSTANCE_PID`
      if [ -z "$PID_TEST" ]
        then
          echo -e "\nOMAR process is no longer running and will be started"
          __restart_instance
        else
          echo "OMAR process $INSTANCE_PID is ok"
      fi
      done
}

# Restart OMAR instance
__restart_instance() {
  echo -e "\nRestarting OMAR.."
  cd $OMAR_HOME
  ./run.sh
  sleep 3
  echo "OMAR process has been restarted on $HOSTNAME" please investigate. > $ERROR_FILE
  __mail_log
}

# Email error log file to specified $MAILTO user
__mail_log()
{
mailx -s "$HOSTNAME OMAR Error" $MAILTO < $ERROR_FILE
}

# Check for errors and clean-up files if applicable
__error_check() {
  if [ -e $ERROR_FILE ]
    then
      if [ -e $PID_FILE ]
        then
          rm $PID_FILE
      fi
      rm $ERROR_FILE
      __get_pid
   else
    exit 0
   fi  
}

# Run script with options based on argument
case $1 in
 run)
      if [ -s $PID_FILE ] # True if PID file exists and has a size greater than zero
        then
          __prereqs
          __monitor
          __error_check
      else
	    __prereqs
        __get_pid
        __monitor
        __error_check  
      fi
      ;;
   *)
      __syntax
      ;;   
esac
