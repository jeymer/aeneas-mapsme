#/system/bin/sh

dir=`dirname "$0"`
hdir=$dir/../..

runs=0

# Setup
echo "MAPSME_EXPERIMENT=OFFLINE_TABLE_RECORDING=string" > $hdir/stoke_properties.txt
echo "MAPSME_TASK_INTERVAL=5000=int" >> $hdir/stoke_properties.txt

rm mapsme.log
touch mapsme.log

for r in `seq 0 $runs`; do

  am start -n com.mapswithme.maps.pro.debug/com.mapswithme.maps.DownloadResourcesActivity

  sleep 10
  
  tstart=`date "+%s"`

  #./monitor.sh &
  #monitor=$!

  sleep 600

  #kill -9 $monitor

  tend=`date "+%s"`

  am broadcast -a stoke.EXIT

  sleep 20

  pm clear com.mapswithme.maps.pro.debug

  echo "Cleared!"

  cat /sdcard/stoke/stoke.log >> mapsme.log
  echo "ERun ${r}: ${tstart} ${tend}" >> mapsme.log


  sleep 10
done

mv mapsme.stamp mapsme.${i}.${j}.${k}.stamp 
