#!/bin/sh
MAIN=baseapi
ARTIFACT=${MAIN}lib
VER=1.0
LIBS=./${ARTIFACT}/build/libs
JAR=$LIBS/$ARTIFACT-$VER.jar
GROUP=com.churchclerk
MVN=../../../apache-maven-3.6.1/bin/mvn

case $1 in
  build)
    ./gradlew clean build 
    ;;

  lib)
    $MVN install:install-file -Dfile=$JAR -DgroupId=$GROUP -DartifactId=$ARTIFACT -Dversion=$VER -Dpackaging=jar
    ;;

  docker)
    #cd app/build/docker
    #docker build -t dongpak/${MAIN}:latest -f jdk8.dockerfile .
    ;;

  run)
    #docker service create --name ${MAIN} --network $2 --secret source=$3,target=churchclerk dongpak/${MAIN}:latest
    ;;

  debug)
    #docker service create --name ${MAIN} --network $2 --secret source=$3,target=churchclerk --env APP_ARG=--debug dongpak/${MAIN}:latest
    ;;

  *)
    echo "util.sh build - runs the gradle build command"
    echo "util.sh lib - installs the library file(s)"
    #echo "util.sh docker - builds the docker image"
    #echo "util.sh run <network> <secret> - starts the service"
    #echo "util.sh debug <network> <secret> - starts the service in debug"
    ;;
esac
