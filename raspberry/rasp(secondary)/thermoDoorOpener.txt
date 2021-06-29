import RPi.GPIO as GPIO
import time
import sys
import signal
import paramiko
from smbus2 import SMBus
from mlx90614 import MLX90614

#GPIO
TRIG = 23 # trigger
ECHO = 24 # echo

GPIO.setmode(GPIO.BCM)

GPIO.setup(TRIG, GPIO.OUT)
GPIO.setup(ECHO, GPIO.IN)

while True:
	
	GPIO.output(TRIG, False)
	time.sleep(1)

	GPIO.output(TRIG, True)
	time.sleep(0.00001)
	GPIO.output(TRIG, False)

	while GPIO.input(ECHO) == 0:
		pulse_start = time.time()

	while GPIO.input(ECHO) == 1:
		pulse_end = time.time()

	pulse_duration = pulse_end - pulse_start

	distance = pulse_duration * 17150
	distance = round(distance, 2)

	if distance > 2 and distance <400:
		print("Distance : " + str(distance - 0.5) + "cm")

	if distance >2 and distance <10:
		bus = SMBus(1)
		sensor = MLX90614(bus, address=0x5A)
		print(sensor.get_ambient())
		print(sensor.get_object_1())
		object = sensor.get_object_1();
		bus.close()
		if object > 30 and object  < 37:
			print("door open")

			cli = paramiko.SSHClient()
			cli.set_missing_host_key_policy(paramiko.AutoAddPolicy)

			server = '192.168.0.34'
			user = 'pi'
			pwd = 'raspberry'

			cli.connect(server, port=22, username=user, password=pwd)
			stdin, stdout, stderr = cli.exec_command('sudo python3 test1/jjh/frontDoorOpen.py')
			lines = stdout.readlines()
			print(''.join(lines))

			cli.close()
			del cli, stdin, stdout, stderr
		else:
			 print("door lock")
	
	time.sleep(3)
