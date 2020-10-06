#!/usr/bin/env sh
cd ../..
./gradlew -Pversion=$1 publish
echo "revnumber=$1 >> \$GITHUB_ENV" >> $GITHUB_WORKSPACE/revnumber.sh
