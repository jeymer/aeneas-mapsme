#!/bin/bash

sdir=/home/acanino/Projects/stokebench/scripts

dirs=(
  l1 
  l2
)

runs=1

#for i in "${dirs[@]}"; do
#  $sdir/stoke.rb -i ./test/$i/stoke.log -b mapsme -o ./test/$i/stoke.dat -O ./test/$i/stats.dat -n $runs -c 100 -s -- machine-android
#done

$sdir/stoke.rb -i ./ -b mapsme -- drain-test
Rscript ./drain.R ./ ./


