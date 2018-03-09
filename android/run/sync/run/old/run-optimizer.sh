#!/system/bin/sh

dir=`dirname "$0"`
hdir=$dir/../../../


taskSamples=2
#taskTimeInSeconds=2
taskTimeInSeconds=30
taskTimeInMs=$((taskTimeInSeconds * 1000))
startupDelayInTasks=2

repeatTimeInSeconds=1200
#repeatTimeInTasks=$((repeatTimeInSeconds / taskTimeInSeconds))
repeatTimeInTasks=10
numRepeats=1

echo "Task(s):${taskTimeInSeconds} Task(ms):${taskTimeInMs} RepeatTime(tsk):${repeatTimeInTasks} Num Repeats:${numRepeats}"

numConfigs=4
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
echo "STOKE_SAMPLING_POLICY=SAMPLE_ALL=string" >> $hdir/stoke_properties.txt
echo "STOKE_LATTICE_POLICY=QUALITY_LATTICE=string" >> $hdir/stoke_properties.txt
echo "STOKE_CONFIGURATION_SET_POLICY=FIXED_SET=string" >> $hdir/stoke_properties.txt

echo "STOKE_NUM_TASK_SAMPLES=${taskSamples}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_TASK_SKIP=${startupDelayInTasks}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_TASK_DELAY=0=int" >> $hdir/stoke_properties.txt 

echo "STOKE_NUM_SELF_OPTIMIZE_TICKS=4=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_SELF_OPTIMIZE_ROUNDS=2=int" >> $hdir/stoke_properties.txt 

echo "STOKE_NUM_REPEAT_TASK_RESET=${repeatTimeInTasks}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_REPEAT_TOTAL=${numRepeats}=int" >> $hdir/stoke_properties.txt 

echo "MAPSME_TASK_INTERVAL=${taskTimeInMs}=int" >> $hdir/stoke_properties.txt
 
am start -n com.mapswithme.maps.pro.experiment/com.mapswithme.maps.DownloadResourcesActivity

#sleep $estMachineTime

#am broadcast -a stoke.EXIT

#sleep 5

#pm clear com.mapswithme.maps.pro.experiment

#cat /sdcard/stoke/stoke.log > epsilon-sall/mapsme.${repeatTimeInSeconds}.${taskTimeInSeconds}.log
