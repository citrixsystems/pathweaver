#!/usr/bin/env bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

home=`cd "$bin/..";pwd`

pushd .
cd $home

java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar server/target/pathweaver-1.1.0-SNAPSHOT.jar start
popd
