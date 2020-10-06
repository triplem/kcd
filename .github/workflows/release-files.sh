#!/usr/bin/env sh
cd ../..
./gradlew -Pversion=$1 publish
<<<<<<< HEAD
echo "revnumber=$1 >> \$GITHUB_ENV" >> $GITHUB_WORKSPACE/revnumber.sh
=======
export revnumber=$1
>>>>>>> 18adf7c... fix: use variable
