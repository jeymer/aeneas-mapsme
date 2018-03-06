#!/bin/bash

dir=`dirname "$0"`

if [ "$#" -ne 4 ]; then
  echo "Usage: pull.sh [GPS Interval] [GPS Priority] [GPS Batching] [Points Sampled]"
  exit
fi

gps_interval=$1
gps_priority=$2
gps_batching=$3
points_sampled=$4

directory=${gps_interval}-${gps_priority}-${gps_batching}

if [ ! -d ${dir}/${directory} ]; then
  echo "ERROR: ${dir}/${directory} does not exhist. Define before continuing!"
  exit 
fi

if [ -e ${dir}/stoke.log ]; then
  rm ${dir}/stoke.log 
fi

adb pull /sdcard/stoke/stoke.log .

if [ ! -e ${dir}/stoke.log ]; then
  echo "ERROR: Failed to grab stoke.log from device!"
  exit
fi

i=0
while [ -e ${dir}/${directory}/${i}-* ]; do
  i=$(($i + 1))
done

name=$i-$points_sampled.log

echo "Saving log to $dir/$directory/$name"

mv ${dir}/stoke.log ${dir}/${directory}/$name
  
