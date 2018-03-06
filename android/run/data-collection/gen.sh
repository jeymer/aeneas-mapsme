#!/bin/bash

sdir=/home/acanino/Projects/stokebench/scripts

  #$sdir/stoke.rb -i $dir/$i/ -b mapsme -m $i -- metric-test 
$sdir/stoke.rb -i stoke.log -b mapsme -o stoke.dat -O stats.dat -T totals.dat -N name -n 2 -c 12 -s -- machine-android

Rscript ./metric.R . .

