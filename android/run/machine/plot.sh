#!/bin/bash

$HOME/Projects/stokebench/scripts/stoke.rb -i pessimist/stoke.log -b mapsme -n 1 -o pessimist/stoke.dat -O pessimist/stats.dat -c 12 -s -- machine-android 
Rscript $HOME/Projects/stokebench/scripts/convergence.R pessimist/stoke.dat 1 12 1 FALSE TRUE

mv energy.pdf configuration.pdf regret.pdf relative.pdf cgroup.pdf pessimist/
#rm stoke.log
