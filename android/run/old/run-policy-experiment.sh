#/bin/bash

dir=`dirname "$0"`
hdir=$dir/../..

intervals=(
  60000
  30000
  10000
  5000
  3000
  2000
  1500
  1000
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

policy=(
  maxq
  minq
  random
  b1
  b2
  b3
)

favored=(
  gps_rate
  gps_accuracy
  sensor_rate
)

ival_len=$((${#intervals[@]}-1))
accuracy_len=$((${#accuracy[@]}-1))
sensor_len=$((${#sensor[@]}-1))

config=0

for f in "${favored[@]}"; do
  # Setup
  for p in "${policy[@]}"; do 
    echo "MAPSME_EXPERIMENT=POLICY=string" > $hdir/stoke_properties.txt
    echo "MAPSME_POLICY=${p}=string"  >> $hdir/stoke_properties.txt
    case "$p" in
      maxq)
        echo "MAPSME_GPS_RATE=${intervals[$ival_len]}=int" >> $hdir/stoke_properties.txt
        echo "MAPSME_GPS_ACCURACY_CHOICE=${accuracy[$accuracy_len]}=int" >> $hdir/stoke_properties.txt
        echo "MAPSME_GPS_SENSOR_CHOICE=${sensor[$sensor_len]}=int" >> $hdir/stoke_properties.txt
        ;;
      minq)
        interval=${intervals[0]}
        accuracyChoice=${accuracy[0]}
        sensorChoice=${sensor[0]}
        case "$f" in
          gps_rate)
            interval=${intervals[$ival_len]}
            ;;
          gps_accuracy)
            accuracyChoice=${accuracy[$accuracy_len]}
            ;;
          gps_rate)
            sensorChoice=${sensor[$sensor_len]}
            ;;
        esac
        echo "MAPSME_GPS_RATE=${interval}=int" >> $hdir/stoke_properties.txt
        echo "MAPSME_GPS_ACCURACY_CHOICE=${accuracyChoice}=int" >> $hdir/stoke_properties.txt
        echo "MAPSME_GPS_SENSOR_CHOICE=${sensorChoice}=int" >> $hdir/stoke_properties.txt
        ;;
      random)
        interval=${intervals[ $(( RANDOM % ${#intervals[@]} ))] }
        accuracyChoice=${intervals[ $(( RANDOM % ${#accuracy[@]} ))] }
        sensorChoice=${intervals[ $(( RANDOM % ${#sensor[@]} ))] }
        echo "MAPSME_GPS_RATE=${interval}=int" >> $hdir/stoke_properties.txt
        echo "MAPSME_GPS_ACCURACY_CHOICE=${accuracyChoice}=int" >> $hdir/stoke_properties.txt
        echo "MAPSME_GPS_SENSOR_CHOICE=${sensorChoice}=int" >> $hdir/stoke_properties.txt
        ;;
      b1)
        echo "MAPSME_LATTICE_POLICY=dummy=string" >> $hdir/stoke_properties.txt
        echo "MAPSME_STOCHASTIC_POLICY=dummy=string" >> $hdir/stoke_properties.txt
        ;;
    esac
  done
done


#for k in `seq 0 ${sensor_len}`; do
#  for j in `seq 0 ${accuracy_len}`; do
#    for i in `seq 0 ${ival_len}`; do
#      # Setup
#      echo "MAPSME_EXPERIMENT=POLICY=string" > $hdir/stoke_properties.txt
#      echo "MAPSME_TASK_INTERVAL=5000=int" >> $hdir/stoke_properties.txt
#      echo "MAPSME_GPS_RATE=${intervals[$i]}=int" >> $hdir/stoke_properties.txt
#      echo "MAPSME_GPS_ACCURACY_CHOICE=${accuracy[$j]}=int" >> $hdir/stoke_properties.txt
#      echo "MAPSME_GPS_SENSOR_CHOICE=${sensor[$k]}=int" >> $hdir/stoke_properties.txt
#
#      am start -n com.mapswithme.maps.pro.debug/com.mapswithme.maps.DownloadResourcesActivity
#
#      sleep 30
#      
#      tstart=`date "+%s"`
#
#      ./monitor.sh &
#      monitor=$!
#
#      sleep 120
#
#      kill -9 $monitor
#
#      tend=`date "+%s"`
#
#      echo "ERun 0: ${tstart} ${tend}" >> mapsme.stamp
#
#      am broadcast -a stoke.EXIT
#
#      sleep 5
#
#      pm clear com.mapswithme.maps.pro.debug
#
#      echo "Cleared!"
#
#      mv mapsme.stamp mapsme.${config}.stamp
#
#      sleep 30
#
#      config=$(($config+1))
#    done
#  done
#done




