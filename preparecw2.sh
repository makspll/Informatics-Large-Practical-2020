#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
ROOT=$DIR/submission

rm -rf $DIR/submission
mkdir $ROOT
mkdir $ROOT/aqmaps
cd $ROOT

#copy source then zip
cp -r $DIR/aqmaps . 
zip -r aqmaps.zip aqmaps

# copy report
cp $DIR/report/main.pdf ilp-report.pdf
mkdir ilp-results

# create output files with executable
java -jar aqmaps/target/aqmaps-0.0.1-SNAPSHOT.jar 01 01 2020 55.944425 -3.188396 9999 9898
java -jar aqmaps/target/aqmaps-0.0.1-SNAPSHOT.jar 02 02 2020 55.944425 -3.188396 9999 9898
java -jar aqmaps/target/aqmaps-0.0.1-SNAPSHOT.jar 03 03 2020 55.944425 -3.188396 9999 9898
java -jar aqmaps/target/aqmaps-0.0.1-SNAPSHOT.jar 04 04 2020 55.944425 -3.188396 9999 9898

java -jar aqmaps/target/aqmaps-0.0.1-SNAPSHOT.jar 05 05 2020 55.944425 -3.188396 9999 9898
java -jar aqmaps/target/aqmaps-0.0.1-SNAPSHOT.jar 06 06 2020 55.944425 -3.188396 9999 9898
java -jar aqmaps/target/aqmaps-0.0.1-SNAPSHOT.jar 07 07 2020 55.944425 -3.188396 9999 9898
java -jar aqmaps/target/aqmaps-0.0.1-SNAPSHOT.jar 08 08 2020 55.944425 -3.188396 9999 9898

java -jar aqmaps/target/aqmaps-0.0.1-SNAPSHOT.jar 09 09 2020 55.944425 -3.188396 9999 9898
java -jar aqmaps/target/aqmaps-0.0.1-SNAPSHOT.jar 10 10 2020 55.944425 -3.188396 9999 9898
java -jar aqmaps/target/aqmaps-0.0.1-SNAPSHOT.jar 11 11 2020 55.944425 -3.188396 9999 9898
java -jar aqmaps/target/aqmaps-0.0.1-SNAPSHOT.jar 12 12 2020 55.944425 -3.188396 9999 9898

mv *.txt ilp-results
mv *.geojson ilp-results

# package 
zip -r ilp-results.zip ilp-results

# clean up
rm -rf output