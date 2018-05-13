#!/bin/bash

while true
do
    echo 'Launching...'
    python3 /home/pi/lib/DictionaryBot/main.py
    echo 'Crashed, relaunching in 20 seconds'
    sleep 20
done
