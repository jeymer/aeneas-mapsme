#!/bin/bash

sdir=/home/acanino/Projects/stokebench/scripts

dirs=(
  oracle
  vbde50
  static2
  pessimist
)

#$sdir/stoke.rb -i oracle/stoke.log -b mapsme -o oracle/stoke.dat -O oracle/stats.dat -T oracle/totals.dat -N oracle -n 2 -c 12 -s -- machine-android
#$sdir/stoke.rb -i vbde50/stoke.log -b mapsme -o vbde50/stoke.dat -O vbde50/stats.dat -T vbde50/totals.dat -N vbde50 -n 6 -c 12 -s -- machine-android
#$sdir/stoke.rb -i static2/stoke.log -b mapsme -o static2/stoke.dat -O static2/stats.dat -T static2/totals.dat -N static2 -n 6 -c 12 -s -- machine-android
#$sdir/stoke.rb -i pessimist/stoke.log -b mapsme -o pessimist/stoke.dat -O pessimist/stats.dat -T pessimist/totals.dat -N pessimist -n 2 -c 12 -s -- machine-android

$sdir/stoke.rb -i . -s -- compare-machine

#Rscript simple.R .
