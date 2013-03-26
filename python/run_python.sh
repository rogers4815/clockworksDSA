#!/bin/sh

if [ -f "$1" ]
then
    echo "Copying file $1 to the android device"
    adb push "$1" /sdcard/sl4a/scripts/
else
    echo "File doesn't exist locally, trying to run it on the android device
    anyway"
fi
adb shell am start -a com.googlecode.android_scripting.action.LAUNCH_FOREGROUND_SCRIPT -n com.googlecode.android_scripting/.activity.ScriptingLayerServiceLauncher -e com.googlecode.android_scripting.extra.SCRIPT_PATH "/sdcard/sl4a/scripts/$1"
