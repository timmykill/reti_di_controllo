#ifndef _DELTA_H_

#define _DELTA_H_
#include <stdio.h>
#include <time.h>
#include <unistd.h>

#define NS_PER_SECOND 1000000000


void save_start_time(struct timespec *start);//metti l'istante iniziale nella struttura timespec
void save_finish_time(struct timespec *finish);//metti l'istante finale nella struttura timespec 
void print_delta(struct timespec start, struct timespec finish);//calcola e stampa la differenza di tempo trascorsa tra i due istanti
#endif
