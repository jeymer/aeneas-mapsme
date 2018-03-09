#!/system/bin/sh

dir=`dirname "$0"`
hdir=$dir/../../../

taskSamples=2
taskTimeInSeconds=30
taskTimeInMs=32500
#taskTimeInMs=2000

startupDelayInTasks=1

repeatTimeInTasks=10

# Setup
echo "STOKE_EXPERIMENT=CONTINUOUS=string" > $hdir/stoke_properties.txt
echo "STOKE_STOCHASTIC_POLICY=VBDE_10=string" >> $hdir/stoke_properties.txt
echo "STOKE_SAMPLING_POLICY=SAMPLE_ALL=string" >> $hdir/stoke_properties.txt
echo "STOKE_LATTICE_POLICY=QUALITY_LATTICE=string" >> $hdir/stoke_properties.txt
echo "STOKE_CONFIGURATION_SET_POLICY=CONTINUOUS_SET=string" >> $hdir/stoke_properties.txt

echo "STOKE_NUM_TASK_SAMPLES=${taskSamples}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_TASK_SKIP=${startupDelayInTasks}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_TASK_DELAY=0=int" >> $hdir/stoke_properties.txt 

echo "STOKE_NUM_REPEAT_TASK_RESET=${repeatTimeInTasks}=int" >> $hdir/stoke_properties.txt 

#echo "STOKE_NUM_REPEAT_TOTAL=1=int" >> $hdir/stoke_properties.txt 
#echo "STOKE_CONTINUOUS_STEPS=4=int" >> $hdir/stoke_properties.txt 
#echo "STOKE_CONTINUOUS_ROUNDS=2=int" >> $hdir/stoke_properties.txt 
#echo "MAPSME_MINLOCATION=5=double" >> $hdir/stoke_properties.txt

echo "STOKE_NUM_REPEAT_TOTAL=1=int" >> $hdir/stoke_properties.txt 
echo "STOKE_CONTINUOUS_STEPS=20=int" >> $hdir/stoke_properties.txt 
echo "STOKE_CONTINUOUS_ROUNDS=10=int" >> $hdir/stoke_properties.txt 

echo "STOKE_MIN_STEPS=15=int" >> $hdir/stoke_properties.txt 
echo "STOKE_MAX_STEPS=30=int" >> $hdir/stoke_properties.txt 

#echo "STOKE_MIN_STEPS=10=int" >> $hdir/stoke_properties.txt 
#echo "STOKE_MAX_STEPS=12=int" >> $hdir/stoke_properties.txt 

echo "STOKE_DIVIDE_SLOTS=5=int" >> $hdir/stoke_properties.txt 
echo "MAPSME_MINLOCATION=25=double" >> $hdir/stoke_properties.txt

echo "MAPSME_TASK_INTERVAL=${taskTimeInMs}=int" >> $hdir/stoke_properties.txt
#echo "MAPSME_GPS_ACCURACY_CHOICE=2=int" >> $hdir/stoke_properties.txt
#echo "MAPSME_GPS_RATE=500=int" >> $hdir/stoke_properties.txt
#echo "MAPSME_SENSOR_RATE_CHOICE=0=int" >> $hdir/stoke_properties.txt
#echo "MAPSME_BATCH=5=int" >> $hdir/stoke_properties.txt
 
am start -n com.mapswithme.maps.pro.experiment/com.mapswithme.maps.DownloadResourcesActivity

#sleep $estMachineTime

#am broadcast -a stoke.EXIT

#sleep 5

#pm clear com.mapswithme.maps.pro.experiment

#cat /sdcard/stoke/stoke.log > epsilon-sall/mapsme.${repeatTimeInSeconds}.${taskTimeInSeconds}.log
