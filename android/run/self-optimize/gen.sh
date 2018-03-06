#!/bin/bash

sdir=/home/acanino/Projects/stokebench/scripts

dirs=(
  o1 
  o2
  o3
  o4
)

runs=1

#for i in "${dirs[@]}"; do
#  $sdir/stoke.rb -i optimizer-drive2/$i/stoke.log -b mapsme -o optimizer-drive2/$i/stoke.dat -O optimizer-drive2/$i/stats.dat -n $runs -c 100 -s -- machine-android
#  ./plot.sh $i $runs $i 12 1 FALSE
#done

#$sdir/stoke.rb -i optimizer-drive2/ -s -- compare-optimizer

Rscript self.R optimizer-drive2/optimizer-last.dat
