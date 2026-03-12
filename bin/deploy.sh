#!/usr/bin/env sh
cd "`dirname $0`"
cd ..
./stopYACY.sh
cd DATA/RELEASE/
rm ../../lib/*
rm -Rf yacy
tar xfz `basename $1`
cp -Rf yacy/* ../../
rm -Rf yacy
cd ../../
chmod 755 *.sh
chmod 755 bin/*.sh
nohup ./startYacy.sh -l
