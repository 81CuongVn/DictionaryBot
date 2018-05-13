#!/usr/bin/env bash

while true
do
    echo 'Launching...'
    python3 main.py
    echo 'Crashed, relaunching in 20 seconds'
    sleep 20
done
