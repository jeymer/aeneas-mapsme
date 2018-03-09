for i in `seq 0 5`; do
  for j in `seq 1 2`; do
    mv mapsme.$i.2.$j.log mapsme.$i.1.$j.log 
  done
done
