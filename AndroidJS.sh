#!/usr/bin/env bash
#AndroidJS
export PROJECT_NAME=${PWD##*/}
echo Compiling $PROJECT_NAME...

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
mkdir output
cp -r $DIR/templates/android18/AndroidJS ./output/AndroidJS
cp -r ./js/ ./output/AndroidJS/assets/
cp -r ./layout/ ./output/AndroidJS/res/layout/
#cp -r $DIR/templates/android18/Java ./output/packagename/
node $DIR/AndroidJS.js ${PWD}

export ANDROID_SDK=~/adt-bundle-mac-x86_64/sdk/tools
$ANDROID_SDK/android update project --target android-19 --path ./output/AndroidJS
cd output/AndroidJS
ant debug
cp ./bin/Activity0-debug.apk ../$PROJECT_NAME-debug.apk
cd ..
sudo rm -r AndroidJS
cd ..

#$ANDROID_SDK/../platform-tools/adb -d install AndroidJS-debug.apk
