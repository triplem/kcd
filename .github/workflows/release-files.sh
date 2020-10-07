#!/usr/bin/env sh
cd ../..
./gradlew -Pversion=$1 publish
export revnumber=$1
echo "revnumber=$1" >> $GITHUB_ENV
echo "REVNUMBER=$1"
echo "REVNUMBER2=$revnumber"
