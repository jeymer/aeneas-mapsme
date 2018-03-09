#!/system/bin/sh

dir=`dirname "$0"`
hdir=$dir/../../../

samplelen=5
numconfigs=33
experiment_time=30

#taskskip=60
#sample=60
#delay=3

taskskip=2
sample=3
delay=1

runs=0

total_time=$(((sample+delay)*samplelen*numconfigs + (samplelen * taskskip) + experiment_time))
runtime=$((sample*samplelen))
echo $total_time

# Setup
echo "STOKE_EXPERIMENT=HARDWARE_SOFTWARE=string" > $hdir/stoke_properties.txt
echo "STOKE_STOCHASTIC_POLICY=NO_STOCHASTIC=string" >> $hdir/stoke_properties.txt
echo "STOKE_SAMPLING_POLICY=SAMPLE_ALL=string" >> $hdir/stoke_properties.txt
echo "STOKE_LATTICE_POLICY=QUALITY_LATTICE=string" >> $hdir/stoke_properties.txt
echo "STOKE_CONFIGURATION_SET_POLICY=FIXED_SET=string" >> $hdir/stoke_properties.txt
echo "STOKE_NUM_TASK_SAMPLES=${sample}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_TASK_SKIP=${taskskip}=int" >> $hdir/stoke_properties.txt 
echo "STOKE_NUM_TASK_DELAY=${delay}=int" >> $hdir/stoke_properties.txt 
echo "MAPSME_TASK_INTERVAL=5000=int" >> $hdir/stoke_properties.txt

for r in `seq 0 $runs`; do

  am start -n com.mapswithme.maps.pro.experiment/com.mapswithme.maps.DownloadResourcesActivity

  sleep $total_time

  am broadcast -a stoke.EXIT

  sleep 5

  pm clear com.mapswithme.maps.pro.experiment

  cat /sdcard/stoke/stoke.log > hardware-software-experiment/mapsme.${runtime}.${r}.log
done
