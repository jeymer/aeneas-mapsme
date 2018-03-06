#/system/bin/sh

dir=`dirname "$0"`
hdir=$dir/../..

intervals=(
  250
  500
  750
  1000
  1500
  2000
  3000
  5000
  10000
)

ival_len=$((${#intervals[@]}-1))

for i in `seq 0 ${ival_len}`; do
    # Setup
    echo "MAPSME_CONFIGURATION_TEST=true=bool" > $hdir/stoke_properties.txt
    echo "MAPSME_TASK_INTERVAL=1000=int" >> $hdir/stoke_properties.txt
    echo "MAPSME_INTERVAL=${intervals[$i]}=int" >> $hdir/stoke_properties.txt

    am start -n com.mapswithme.maps.pro.debug/com.mapswithme.maps.DownloadResourcesActivity

    sleep 10

    am broadcast -a stoke.EXIT

    sleep 2

    pm clear com.mapswithme.maps.pro.debug

    sleep 10

    cp /sdcard/stoke/stoke.log ./stoke.log.${i}
done




