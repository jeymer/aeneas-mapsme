#!/bin/bash

dir=`dirname "$0"`

if [ "$#" -ne 2 ]; then
  echo "Usage: pull.sh [MACHINE TYPE] [NUM RUNS]"
  exit
fi

machine_type=$1
num_runs=$2

directory=$machine_type

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

name=$i-$num_runs.log

echo "Saving log to $dir/$directory/$name"

mv ${dir}/stoke.log ${dir}/${directory}/$name
  
