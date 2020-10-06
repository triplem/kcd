#!/usr/bin/env sh
echo "revnumber=$1" >> $HOME/revnumber.sh
echo "HOME: $HOME"
cd ../..
./gradlew -Pversion=$1 publish
