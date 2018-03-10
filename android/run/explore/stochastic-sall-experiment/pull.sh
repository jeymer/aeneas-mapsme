#!/bin/bash

samples=(
  2
  3
  5
)

label=$1

samples_len=$((${#samples[@]}-1))
mkdir $label

for i in `seq 0 ${samples_len}`; do
  sample=${samples[$i]}
  adb pull /data/local/tmp/stoke/mapsme/run/stochastic-sall-experiment/mapsme.$sample.log $1/
done

