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
	USART_Init(MYUBRR);
	
    while (1) {
		// TODO
		USART_Transmit(USART_Receive());
    }
	
	return 1;
}

