#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
ROOT=$DIR/submission/aqmaps

rm -rf $DIR/submission
mkdir $DIR/submission
mkdir $ROOT

#copy source then zip
cp -r $DIR/aqmaps $ROOT 
zip -r $ROOT/aqmaps.zip $ROOT/aqmaps

# copy report
cp $DIR/report/main.pdf $ROOT/report.pdf





