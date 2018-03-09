#!/system/bin/sh

dir=`dirname "$0"`
hdir=$dir/../../../


taskSamples=10
taskTimeInSeconds=30
taskTimeInMs=$((taskTimeInSeconds * 1000))
startupDelayInTasks=10
taskDelays=2

numConfigs=15

estMachineTime=$((startupDelayInTasks * taskTimeInSeconds + (numConfigs * ((taskTimeInSeconds * taskSamples) + (taskTimeInSeconds * taskDelays)))))

echo "Estimated Time in Seconds: ${estMachineTime}"

# Setup
echo "STOKE_EXPERIMENT=OFFLINE_COLLECT=string" > $hdir/stoke_properties.txt
echo "STOKE_STOCHASTIC_POLICY=NO_STOCHASTIC=string" >> $hdir/stoke_properties.txt
echo "STOKE_SAMPLING_POLICY=SAMPLE_ALL=string" >> $hdir/stoke_properties.txt
echo "STOKE_LATTICE_POLICY=QUALITY_LATTICE=string" >> $hdir/stoke_properties.txt
echo "STOKE_CONFIGURATION_SET_POLICY=FIXED_SET=string" >> $hdir/stoke_properties.txt

echo "STOKE_NUM_TASK_SAMPLES=${taskSamples}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_TASK_SKIP=${startupDelayInTasks}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_TASK_DELAY=${taskDelays}=int" >> $hdir/stoke_properties.txt 

echo "STOKE_NUM_REPEAT_TASK_RESET=0=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_REPEAT_TOTAL=0=int" >> $hdir/stoke_properties.txt 

echo "MAPSME_TASK_INTERVAL=${taskTimeInMs}=int" >> $hdir/stoke_properties.txt
 
am start -n com.mapswithme.maps.pro.experiment/com.mapswithme.maps.DownloadResourcesActivity

#sleep $estMachineTime

#am broadcast -a stoke.EXIT

#sleep 5

#pm clear com.mapswithme.maps.pro.experiment

#cat /sdcard/stoke/stoke.log > epsilon-sall/mapsme.${repeatTimeInSeconds}.${taskTimeInSeconds}.log
