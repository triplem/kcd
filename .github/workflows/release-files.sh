#!/usr/bin/env sh
cd ../..
./gradlew -Pversion=$1 publish
echo "export revnumber=$1" >> $GITHUB_WORKSPACE/revnumber.sh
