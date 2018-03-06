#!/bin/bash

sdir=/home/acanino/Projects/stokebench/scripts

$sdir/stoke.rb -i p1-non1s/stoke.log -b mapsme -o p1-non1s/stoke.dat -O p1-non1s/stats.dat -T p1-non1s/totals.dat -N p1-non1s -n 1 -c 12 -s -- machine-android
$sdir/stoke.rb -i p1-1s/stoke.log -b mapsme -o p1-1s/stoke.dat -O p1-1s/stats.dat -T p1-1s/totals.dat -N p1-1s -n 1 -c 12 -s -- machine-android
$sdir/stoke.rb -i p2-non1s/stoke.log -b mapsme -o p2-non1s/stoke.dat -O p2-non1s/stats.dat -T p2-non1s/totals.dat -N p2-non1s -n 1 -c 12 -s -- machine-android
$sdir/stoke.rb -i p2-1s/stoke.log -b mapsme -o p2-1s/stoke.dat -O p2-1s/stats.dat -T p2-1s/totals.dat -N p2-1s -n 1 -c 12 -s -- machine-android
$sdir/stoke.rb -i p3-non1s/stoke.log -b mapsme -o p3-non1s/stoke.dat -O p3-non1s/stats.dat -T p3-non1s/totals.dat -N p3-non1s -n 1 -c 12 -s -- machine-android
$sdir/stoke.rb -i p3-1s/stoke.log -b mapsme -o p3-1s/stoke.dat -O p3-1s/stats.dat -T p3-1s/totals.dat -N p3-1s -n 1 -c 12 -s -- machine-android
$sdir/stoke.rb -i p4-non1s/stoke.log -b mapsme -o p4-non1s/stoke.dat -O p4-non1s/stats.dat -T p4-non1s/totals.dat -N p4-non1s -n 1 -c 12 -s -- machine-android
$sdir/stoke.rb -i p4-1s/stoke.log -b mapsme -o p4-1s/stoke.dat -O p4-1s/stats.dat -T p4-1s/totals.dat -N p4-1s -n 1 -c 12 -s -- machine-android

$sdir/stoke.rb -i . -s -- compare-overhead > overhead.txt

#Rscript simple.R .
