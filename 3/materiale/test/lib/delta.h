#ifndef _DELTA_H_

#define _DELTA_H_
#include <stdio.h>
#include <time.h>
#include <unistd.h>

#define NS_PER_SECOND 1000000000
static struct timespec start, finish, delta;


void save_start_time(void);
void save_finish_time(void);
void print_delta(void);
#endif
