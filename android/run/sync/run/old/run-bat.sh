#/system/bin/sh

dir=`dirname "$0"`
hdir=$dir/../..

rate=10000

# Setup
echo "MAPSME_EXPERIMENT=CONFIGURATION=string" > $hdir/stoke_properties.txt
echo "MAPSME_TASK_INTERVAL=5000=int" >> $hdir/stoke_properties.txt
echo "MAPSME_GPS_RATE=$rate=int" >> $hdir/stoke_properties.txt
echo "MAPSME_GPS_ACCURACY_CHOICE=2=int" >> $hdir/stoke_properties.txt
echo "MAPSME_SENSOR_RATE_CHOICE=2=int" >> $hdir/stoke_properties.txt
echo "MAPSME_SAMPLING_POLICY=SAMPLE_PRIORITY=string" >> $hdir/stoke_properties.txt
echo "MAPSME_SAMPLES=1=int" >> $hdir/stoke_properties.txt 
echo "MAPSME_TASKSKIP=0=int" >> $hdir/stoke_properties.txt 
echo "MAPSME_TASKDELAY=0=int" >> $hdir/stoke_properties.txt 

am start -n com.mapswithme.maps.pro.debug/com.mapswithme.maps.DownloadResourcesActivity > /dev/null