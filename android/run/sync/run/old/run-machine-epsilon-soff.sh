#!/system/bin/sh

dir=`dirname "$0"`
hdir=$dir/../../../


taskSamples=1
taskTimeInSeconds=5
taskTimeInMs=$((taskTimeInSeconds * 1000))
startupDelayInTasks=6

repeatTimeInSeconds=60
repeatTimeInTasks=$((repeatTimeInSeconds / taskTimeInSeconds))
numRepeats=10

echo "Task(s):${taskTimeInSeconds} Task(ms):${taskTimeInMs} RepeatTime(tsk):${repeatTimeInTasks} Num Repeats:${numRepeats}"

numConfigs=0
paddingInSeconds=60

estMachineTime=$((startupDelayInTasks * taskTimeInSeconds + (repeatTimeInSeconds + taskTimeInSeconds) + (numConfigs * taskTimeInSeconds * taskSamples)))
estMachineTime=$(((estMachineTime * numRepeats) + paddingInSeconds))

safeMachineTime=$((startupDelayInTasks * taskTimeInSeconds + (repeatTimeInSeconds + taskTimeInSeconds) + (numConfigs * (taskTimeInSeconds + 1) * taskSamples)))
safeMachineTime=$(((safeMachineTime * numRepeats) + paddingInSeconds))

echo "Estimated Time in Seconds: ${estMachineTime}"
echo "Safe Time in Seconds: ${safeMachineTime}"


# Setup
echo "STOKE_EXPERIMENT=MACHINE=string" > $hdir/stoke_properties.txt
echo "STOKE_STOCHASTIC_POLICY=EPSILON_GREEDY=string" >> $hdir/stoke_properties.txt
echo "STOKE_SAMPLING_POLICY=OFFLINE_PROFILE=string" >> $hdir/stoke_properties.txt
echo "STOKE_LATTICE_POLICY=QUALITY_LATTICE=string" >> $hdir/stoke_properties.txt
echo "STOKE_CONFIGURATION_SET_POLICY=FIXED_SET=string" >> $hdir/stoke_properties.txt

echo "STOKE_NUM_TASK_SAMPLES=${taskSamples}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_TASK_SKIP=${startupDelayInTasks}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_TASK_DELAY=0=int" >> $hdir/stoke_properties.txt 

echo "STOKE_NUM_REPEAT_TASK_RESET=${repeatTimeInTasks}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_REPEAT_TOTAL=${numRepeats}=int" >> $hdir/stoke_properties.txt 

echo "MAPSME_TASK_INTERVAL=${taskTimeInMs}=int" >> $hdir/stoke_properties.txt
 
am start -n com.mapswithme.maps.pro.experiment/com.mapswithme.maps.DownloadResourcesActivity

sleep $estMachineTime

am broadcast -a stoke.EXIT

sleep 5

pm clear com.mapswithme.maps.pro.experiment

cat /sdcard/stoke/stoke.log > epsilon-soff/mapsme.${repeatTimeInSeconds}.log
