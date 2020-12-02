#ifndef SHARED_H
#define SHARED_H
#include <time.h>


void save_time(struct timespec *t);
void print_delta(struct timespec start, struct timespec finish);

#endif
