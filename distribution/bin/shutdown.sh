#!/bin/bash

cd `dirname $0`/../target
target_dir=`pwd`

pid=`ps ax | grep -i 'G-Cloud.server' | grep ${target_dir} | grep java | grep -v grep | awk '{print $1}'`
if [ -z "$pid" ] ; then
        echo "no G-Cloud server running."
        exit -1;
fi

echo "the G-Cloud server(${pid}) is running..."

kill ${pid}

echo "send shutdown request to G-Cloud server(${pid}) OK"