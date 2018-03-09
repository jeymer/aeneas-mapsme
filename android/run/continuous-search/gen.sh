#!/bin/bash

sdir=/home/acanino/Projects/stokebench/scripts
dir=$1

#dirs=(
#  60 
##  65
#  70
#)

dirs=(
  20
#  25
  30
)

#$sdir/stoke.rb -i $dir/20/ -b mapsme -m 20 -- metric-test 
$sdir/stoke.rb -i $dir/20/stoke.log -b mapsme -o $dir/20/stoke.dat -O $dir/20/stats.dat -T $dir/20/totals.dat -N 20 -n 1 -c 12 -s -- machine-android

$sdir/stoke.rb -i $dir/30/stoke.log -b mapsme -o $dir/30/stoke.dat -O $dir/30/stats.dat -T $dir/30/totals.dat -N 30 -n 1 -c 12 -s -- machine-android

  #Rscript ./metric.R $dir/$i/ $dir/$i/ $i

  #cp $dir/$i/drain-rate.pdf $dir/$i-drain-rate.pdf
  #cp $dir/$i/avg-joules.pdf $dir/$i-avg-joules.pdf
  #cp $dir/$i/win-joules.pdf $dir/$i-win-joules.pdf
  #cp $dir/$i/rate-time.pdf $dir/$i-rate-time.pdf
