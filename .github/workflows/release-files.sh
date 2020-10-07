#!/usr/bin/env sh
cd ../..
./gradlew -Pversion=$1 publish
export revnumber=$1
