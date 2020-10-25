#include "delta.h"

void sub_timespec(struct timespec t1, struct timespec t2, struct timespec *td)
{
    td->tv_nsec = t2.tv_nsec - t1.tv_nsec;
    td->tv_sec  = t2.tv_sec - t1.tv_sec;
    if (td->tv_sec > 0 && td->tv_nsec < 0)
    {
        td->tv_nsec += NS_PER_SECOND;
        td->tv_sec--;
    }
    else if (td->tv_sec < 0 && td->tv_nsec > 0)
    {
        td->tv_nsec -= NS_PER_SECOND;
        td->tv_sec++;
    }
}

void save_start_time(void){
	clock_gettime(CLOCK_REALTIME, &start);
}

void save_finish_time(void){
	clock_gettime(CLOCK_REALTIME, &finish);
}

void print_delta(void){
	sub_timespec(start, finish, &delta);
	printf("%d.%.9ld", (int)delta.tv_sec, delta.tv_nsec);
}
