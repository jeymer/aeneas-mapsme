#!/system/bin/sh

dir=`dirname "$0"`
hdir=$dir/../../../

taskSamples=1
taskTimeInSeconds=30
taskTimeInMs=$((taskTimeInSeconds * 1000))

startupDelayInTasks=2

repeatTimeInTasks=0

# Setup
echo "STOKE_EXPERIMENT=OPTIMIZER=string" > $hdir/stoke_properties.txt
echo "STOKE_STOCHASTIC_POLICY=VBDE_10=string" >> $hdir/stoke_properties.txt
echo "STOKE_SAMPLING_POLICY=SAMPLE_ALL=string" >> $hdir/stoke_properties.txt
echo "STOKE_LATTICE_POLICY=QUALITY_LATTICE=string" >> $hdir/stoke_properties.txt
echo "STOKE_CONFIGURATION_SET_POLICY=FIXED_SET=string" >> $hdir/stoke_properties.txt

echo "STOKE_NUM_TASK_SAMPLES=${taskSamples}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_TASK_SKIP=${startupDelayInTasks}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_TASK_DELAY=0=int" >> $hdir/stoke_properties.txt 

echo "STOKE_NUM_SELF_OPTIMIZE_TICKS=15=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_SELF_OPTIMIZE_ROUNDS=3=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_REPEAT_TOTAL=0=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_REPEAT_TASK_RESET=0=int" >> $hdir/stoke_properties.txt 
echo "STOKE_CONTINUOUS_TASK_SAMPLES=4=int" >> $hdir/stoke_properties.txt 

#echo "STOKE_SELF_OPTIMIZE_FIXED_INTERVAL=60000=int" >> $hdir/stoke_properties.txt 
echo "STOKE_SELF_OPTIMIZE_FINAL_TICKS=10=int" >> $hdir/stoke_properties.txt 

echo "MAPSME_TASK_INTERVAL=${taskTimeInMs}=int" >> $hdir/stoke_properties.txt 

am start -n com.mapswithme.maps.pro.experiment/com.mapswithme.maps.DownloadResourcesActivity

#sleep $estMachineTime

#am broadcast -a stoke.EXIT

#sleep 5

#pm clear com.mapswithme.maps.pro.experiment

#cat /sdcard/stoke/stoke.log > epsilon-sall/mapsme.${repeatTimeInSeconds}.${taskTimeInSeconds}.log
