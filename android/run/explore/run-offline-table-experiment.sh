#/system/bin/sh

dir=`dirname "$0"`
hdir=$dir/../..

sample=1
samplelen=5
numconfigs=3
experiment_time=600
taskskip=10

runs=2

total_time=$((sample*samplelen*numconfigs + (samplelen * taskskip) + experiment_time))


echo $total_time

# Setup
echo "MAPSME_EXPERIMENT=OFFLINE_TABLE=string" > $hdir/stoke_properties.txt
echo "MAPSME_TASK_INTERVAL=5000=int" >> $hdir/stoke_properties.txt
echo "MAPSME_SAMPLING_POLICY=SAMPLE_PRIORITY=string" >> $hdir/stoke_properties.txt
echo "MAPSME_SAMPLES=$sample=int" >> $hdir/stoke_properties.txt 

rm offline-table-experiment/mapsme.off.log
touch offline-table-experiment/mapsme.off.log

for r in `seq 0 $runs`; do

  am start -n com.mapswithme.maps.pro.debug/com.mapswithme.maps.DownloadResourcesActivity

  sleep $total_time

  am broadcast -a stoke.EXIT

  sleep 20

  pm clear com.mapswithme.maps.pro.debug

  cat /sdcard/stoke/stoke.log >> offline-table-experiment/mapsme.off.log

  sleep 10
done
