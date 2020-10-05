#!/usr/bin/env sh
cd ../..
./gradlew -Pversion=$1 publish
export REVNUMBER=$1