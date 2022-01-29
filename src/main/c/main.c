/*
 * valocr
 * main.c
 * for ATmega1284P
 *
 * Created: 7/23/2021 12:14:57 PM
 * Author : jstnf
 */ 

#include <avr/io.h>
#include "usart.h"

int main(void) {
	DDRB = 0x07; PORTB = 0x00;
	USART_Init(MYUBRR);
	
    while (1) {
		// TODO
		unsigned char data = USART_Receive();
		switch (data) {
			case 0x00:
				PORTB = 0x01;
				break;
			case 0x01:
				PORTB = 0x02;
				break;
			case 0x02:
				PORTB = 0x04;
				break;
			case 0x69:
				PORTB = 0x05;
				break;
			default:
				PORTB = 0x00;
				break;
		}
    }
	
	return 1;
}

