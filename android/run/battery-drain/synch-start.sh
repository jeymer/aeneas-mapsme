#!/bin/bash

adb push run-drain.sh /data/local/tmp/stoke/paper-bench/binary-mapsme/run
adb shell /data/local/tmp/stoke/paper-bench/binary-mapsme/run/run-drain.sh
