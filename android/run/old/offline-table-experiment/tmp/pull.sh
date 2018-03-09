#!/bin/bash

for i in `seq 0 5`; do
  for j in `seq 1 2`; do
    adb pull /data/local/tmp/stoke/mapsme/run/offline-table-experiment/mapsme.$i.2.$j.log 
  done
done
