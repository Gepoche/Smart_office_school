#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <wiringPi.h>
#include <wiringSerial.h>

int main() {
	int fd;
	int data;
	char signal[5];

	if(wiringPiSetupGpio() == -1)
		return 1;
	if((fd = serialOpen("/dev/ttyAMA0", 115200)) < 0)
		return 1;

	serialPutchar(fd, '\n');
	while(1) {
		scanf("%s", signal);
		for(int i = 0; i < 5; i++) {
			if(signal[i] == '\0') {
				serialPutchar(fd, '\n');
				fflush(stdout);
				break;
			}
			serialPutchar(fd, signal[i]);
			fflush(stdout);
		}
	}
return 0;
}
