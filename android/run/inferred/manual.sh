#!/bin/bash

sdir=/home/acanino/Projects/stokebench/scripts
dir=$1


Rscript ./30-manual.R inf-drive/d4/30/ inf-drive/d4/30/ 30
#Rscript ./25-manual.R $dir/25/ $dir/25/ 25
Rscript ./20-manual.R inf-drive/d4/20/ inf-drive/d4/20/ 20

Rscript ./30-bike-manual.R inf-bike/b1/30/ inf-bike/b1/30/ 30
Rscript ./20-bike-manual.R inf-bike/b1/20/ inf-bike/b1/20/ 20

