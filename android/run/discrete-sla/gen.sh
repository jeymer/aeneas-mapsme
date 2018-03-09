#!/bin/bash

sdir=/home/acanino/Projects/stokebench/scripts

dirs=(
  20
  25
  30
)

for i in "${dirs[@]}"; do
  #$sdir/stoke.rb -i ./dis-drive/$i/ -b mapsme -m $i -- metric-test 
  #$sdir/stoke.rb -i ./dis-drive/$i/stoke.log -b mapsme -o ./dis-drive/$i/stoke.dat -O ./dis-drive/$i/stats.dat -T ./dis-drive/$i/totals.dat -N $i -n 4 -c 20 -s -- machine-android
  Rscript ./metric.R ./dis-drive/$i/ ./dis-drive/$i/ $i
done
