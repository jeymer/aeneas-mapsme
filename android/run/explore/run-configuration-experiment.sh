#/system/bin/sh

dir=`dirname "$0"`
hdir=$dir/../..

intervals=(
  10000
  5000
  3000
  2000
  1500
  1000
  750
  500
  250
) 

accuracy=(
  0
  1
) 

sensor=(
  0
  1
  2
) 

ival_len=$((${#intervals[@]}))
accuracy_len=$((${#accuracy[@]}))
sensor_len=$((${#sensor[@]}))

runs=0

experiment_time=120
taskskip=10 

total_time=$((taskskip + experiment_time))

k=0
while [ $k -lt ${sensor_len} ]; do
#for k in `seq 0 ${sensor_len}`; do
  j=0
  while [ $j -lt ${accuracy_len} ]; do
  #for j in `seq 0 ${accuracy_len}`; do
    i=0
    while [ $i -lt ${ival_len} ]; do
    #for i in `seq 0 ${ival_len}`; do
      # Setup
      echo "MAPSME_EXPERIMENT=CONFIGURATION=string" > $hdir/stoke_properties.txt
      echo "MAPSME_TASK_INTERVAL=5000=int" >> $hdir/stoke_properties.txt
      echo "MAPSME_GPS_RATE=${intervals[$i]}=int" >> $hdir/stoke_properties.txt
      echo "MAPSME_GPS_ACCURACY_CHOICE=${accuracy[$j]}=int" >> $hdir/stoke_properties.txt
      echo "MAPSME_SENSOR_RATE_CHOICE=${sensor[$k]}=int" >> $hdir/stoke_properties.txt
      echo "MAPSME_SAMPLING_POLICY=SAMPLE_PRIORITY=string" >> $hdir/stoke_properties.txt
      echo "MAPSME_SAMPLES=1=int" >> $hdir/stoke_properties.txt 
      echo "MAPSME_TASKSKIP=$taskskip=int" >> $hdir/stoke_properties.txt 

      echo "Running mapsme.${i}.${j}.${k}"

      rm offline-table-experiment/mapsme.${i}.${j}.${k}.log
      touch offline-table-experiment/mapsme.${i}.${j}.${k}.log

      for r in `seq 0 $runs`; do

        am start -n com.mapswithme.maps.pro.debug/com.mapswithme.maps.DownloadResourcesActivity

        sleep $total_time

        am broadcast -a stoke.EXIT

        sleep 5

        pm clear com.mapswithme.maps.pro.debug

        echo "==RUN-${r}==" >> offline-table-experiment/mapsme.${i}.${j}.${k}.log
        cat /sdcard/stoke/stoke.log >> offline-table-experiment/mapsme.${i}.${j}.${k}.log

        sleep 20
      done
      echo "Finished mapsme.${i}.${j}.${k}"
      i=$(($i + 1))
    done
    j=$(($j + 1))
  done
  k=$(($k + 1))
done




