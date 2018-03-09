#!/bin/bash

for i in `seq 0 8`; do
  for j in `seq 0 1`; do
    for k in `seq 0 2`; do
      adb pull /data/local/tmp/stoke/mapsme/run/offline-table-experiment/mapsme.$i.$j.$k.log 
    done
  done
done
