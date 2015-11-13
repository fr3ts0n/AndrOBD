#!/bin/sh
export JARFILE=$HOME/src/j/info-proxy/AgnisServer/dist/AgnisServer.jar
if [ "$1" != "" ]; then
  # stty -F $1 38400 sane ignbrk -brkint -icrnl -imaxbel -opost -onlcr -isig -icanon -iexten -echo -echoe -echok -echoctl -echoke
  stty -F $1 38400 sane ignbrk -brkint -icrnl -imaxbel -isig -icanon -iexten -echo -echoe -echok -echoctl -echoke
else
  echo "NO serial port specified, starting in DEMO-Mode"
fi
java -cp .:$JARFILE com.fr3ts0n.ecu.ObdTestFrame $*
