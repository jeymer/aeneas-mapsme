for i in `seq 0 5`; do
  for j in `seq 0 1`; do
    mv mapsme.$i.1.$j.log mapsme.$i.2.$j.log 
  done
done
