#!/usr/bin/env sh
echo "revnumber=$1" >> revnumber.sh
cd ../..
./gradlew -Pversion=$1 publish
