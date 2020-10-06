#!/usr/bin/env sh
cd ../..
./gradlew -Pversion=$1 publish
echo "revnumber=$1" >> $HOME/revnumber.sh
