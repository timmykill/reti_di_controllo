#ifndef _SHARED_H_
#define _SHARED_H_
#include <time.h>

#ifdef TEST
#define LOGD(...) printf(__VA_ARGS__)
#else
#define LOGD(...)
#endif

int die(char * msg, int ret);
void sub_timespec(struct timespec t1, struct timespec t2, struct timespec *td);
void print_delta(struct timespec start, struct timespec finish);
void save_time(struct timespec *t);
#endif
