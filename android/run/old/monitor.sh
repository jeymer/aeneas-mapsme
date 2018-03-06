#/system/bin/sh

i=0

while true; do
  tstart=`date "+%s"`

  sleep 5

  tend=`date "+%s"`

  echo "ETask ${i}: ${tstart} ${tend}" >> mapsme.stamp

  i=$((i+1))
done
