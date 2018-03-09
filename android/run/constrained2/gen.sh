#!/bin/bash

sdir=/home/acanino/Projects/stokebench/scripts
dir=$1

dirs=(
  60 
#  65
  70
)

for i in "${dirs[@]}"; do
#  $sdir/stoke.rb -i $dir/$i/ -b mapsme -m $i -- metric-test 
  Rscript ./metric.R $dir/$i/ $dir/$i/ $i
done
