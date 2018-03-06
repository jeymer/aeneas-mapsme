#!/system/bin/sh

dir=`dirname "$0"`
hdir=$dir/../../../

taskSamples=2
#taskTimeInSeconds=30
taskTimeInMs=32500

startupDelayInTasks=0

repeatTimeInTasks=30

# Setup
echo "STOKE_EXPERIMENT=IGNORE=string" > $hdir/stoke_properties.txt
echo "STOKE_STOCHASTIC_POLICY=NO_STOCHASTIC=string" >> $hdir/stoke_properties.txt
#echo "STOKE_STOCHASTIC_POLICY=NO_STOCHASTIC=string" >> $hdir/stoke_properties.txt
echo "STOKE_SAMPLING_POLICY=SAMPLE_ALL=string" >> $hdir/stoke_properties.txt
echo "STOKE_LATTICE_POLICY=QUALITY_LATTICE=string" >> $hdir/stoke_properties.txt
echo "STOKE_CONFIGURATION_SET_POLICY=FIXED_SET=string" >> $hdir/stoke_properties.txt

echo "STOKE_NUM_TASK_SAMPLES=${taskSamples}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_TASK_SKIP=${startupDelayInTasks}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_TASK_DELAY=0=int" >> $hdir/stoke_properties.txt 

echo "STOKE_NUM_REPEAT_TASK_RESET=${repeatTimeInTasks}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_REPEAT_TOTAL=1=int" >> $hdir/stoke_properties.txt 
echo "STOKE_REWARD=BATTERY=string" >> $hdir/stoke_properties.txt 

echo "MAPSME_TASK_INTERVAL=${taskTimeInMs}=int" >> $hdir/stoke_properties.txt
echo "MAPSME_GPS_ACCURACY_CHOICE=0=int" >> $hdir/stoke_properties.txt
echo "MAPSME_GPS_RATE=1000=int" >> $hdir/stoke_properties.txt
echo "MAPSME_SENSOR_RATE_CHOICE=0=int" >> $hdir/stoke_properties.txt
echo "MAPSME_BATCH=5=int" >> $hdir/stoke_properties.txt 
 
am start -n com.mapswithme.maps.pro.experiment/com.mapswithme.maps.DownloadResourcesActivity

