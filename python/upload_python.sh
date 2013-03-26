#!/bin/sh

if [ -f "$1" ]
then
    echo "Copying file to the android device"
    adb push "$1" /sdcard/sl4a/scripts/
else
    echo "File doesn't exist!"
fi
