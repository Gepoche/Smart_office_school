import sys
import serial
import time

ser = serial.Serial('/dev/ttyAMA0')

serialCommand = 'D0_O\n'

for i in range(len(serialCommand)):
  ser.write(bytes(serialCommand[i], encoding='utf-8'))
  
time.sleep(5)

serialCommand = 'D0_C\n'
for i in range(len(serialCommand)):
  ser.write(bytes(serialCommand[i], encoding='utf-8'))
