#!/bin/bash

dir=`dirname "$0"`

if [ "$#" -ne 3 ]; then
  echo "Usage: pull.sh [TOP] [PHONE ID] [TAG]"
  exit
fi

top=$1
phone_id=$2
tag=$3

directory=$top/r$phone_id

adb pull /sdcard/stoke/stoke.log $dir/$directory/

if [ ! -e ${dir}/$directory/stoke.log ]; then
  echo "ERROR: Failed to grab stoke.log from device!"
  exit
fi

name=$phone_id-$tag.log

cp $dir/$directory/stoke.log $dir/$directory/$name

