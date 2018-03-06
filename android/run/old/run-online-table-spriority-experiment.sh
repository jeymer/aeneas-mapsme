#/system/bin/sh

dir=`dirname "$0"`
hdir=$dir/../..

samples=(
  2
  3
  5
)

samplelen=5
numconfigs=3
experiment_time=600
taskskip=10

runs=2

samples_len=$((${#samples[@]}-1))

for i in `seq 0 ${samples_len}`; do
  sample=${samples[$i]}
  total_time=$((sample*samplelen*numconfigs + (samplelen * taskskip) + experiment_time))
  echo $total_time

  # Setup
  echo "MAPSME_EXPERIMENT=ONLINE_TABLE=string" > $hdir/stoke_properties.txt
  echo "MAPSME_STOCHASTIC_POLICY=EPSILON_GREEDY=string" >> $hdir/stoke_properties.txt
  echo "MAPSME_TASK_INTERVAL=5000=int" >> $hdir/stoke_properties.txt
  echo "MAPSME_SAMPLING_POLICY=SAMPLE_PRIORITY=string" >> $hdir/stoke_properties.txt
  echo "MAPSME_SAMPLES=$sample=int" >> $hdir/stoke_properties.txt 

  rm online-table-spriority-experiment/mapsme.$sample.log
  touch online-table-spriority-experiment/mapsme.$sample.log

  for r in `seq 0 $runs`; do

    am start -n com.mapswithme.maps.pro.debug/com.mapswithme.maps.DownloadResourcesActivity

    sleep $total_time

    am broadcast -a stoke.EXIT

    sleep 20

    pm clear com.mapswithme.maps.pro.debug

    cat /sdcard/stoke/stoke.log >> online-table-spriority-experiment/mapsme.$sample.log

    sleep 10
  done
done
