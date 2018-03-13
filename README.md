# Aeneas MapsWithMe Benchmark

Prerequisites
------------

This file contains android MapsWithMe modified to use Aeneas.

MapsWithMe is required in the sense that this repository is a drop in replacement for the android folder within the MapsWithMe project. We refer interested developers to the [MapsWithMe Project](https://github.com/mapsme/omim) to install the android application. Once installed, please replace the android folder with this repository.

Installation
------------

```./gradlew installWebRelease``` will build the application.

Usage
------------

For experimental purposes, MapsWithMe built with Aeneas needs to be bootstraped with startup scripts. These are located in the $AENEAS_MAPSME_HOME/run directory.

Technical
------------

[We provide a technical document, detailing raw data and results for our experiments](https://github.com/pl-aeneas/aeneas/blob/master/supp.pdf). For further details, please consult the technical document along with [MapsWithMe application](https://github.com/pl-aeneas/aeneas-mapsme) repository.




