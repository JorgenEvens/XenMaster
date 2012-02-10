#!/bin/bash

# Setup build evironment
hash dpkg &>-
if [[ $? == 0 ]]; then
	export JAVA_HOME="/usr/lib/jvm/java-1.7.0-openjdk-`dpkg --print-architecture`"
else
	export JAVA_HOME="/usr/lib/jvm/java-1.7.0"
fi
JAVA="$JAVA_HOME/jre/bin/java"
ALL="1"
ROOT=`pwd`

update() {
	cd $ROOT
	git pull
}

build() {
	cd $ROOT
	mvn clean install -Dmaven.test.skip=true
}

documentation() {
	cd $ROOT
	mvn javadoc:javadoc
}

start() {
	stop
	cd $ROOT/conf
	$JAVA -jar ../target/XenMaster-jar-with-dependencies.jar >> output.log &
	PID=$!
	echo $PID > instance.pid
	echo "Java PID: " $PID
	echo "Server output will be redirected to conf/output.log"
}

stop() {
	cd $ROOT/conf
	if [[ -f instance.pid ]]; then
		PID=`cat instance.pid`
		kill $PID
		echo "Killed server " $PID
		rm instance.pid
	fi
}

all() {
	stop
	update
	build
	start
}

log() {
	cd $ROOT/conf
	tail -f output.log
}

exec() {
	case $1 in
		"build")
			build
		;;
		"update")
			update
		;;
		"start")
			start
		;;
		"stop")
			stop
		;;
		"doc")
			documentation
		;;
		"log")
			log
		;;
		"all")
			all
		;;
	esac
}

for i in $*; do
	ALL="0"
	exec $i
done

if [[ $ALL -eq "1" ]]; then
	exec all
fi
