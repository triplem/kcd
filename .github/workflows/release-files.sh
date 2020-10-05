#!/usr/bin/env sh
cd ../..
./gradlew -Pversion=$1 publish
echo "::set-env name=revnumber::$1"
echo "revnumber:$1"
