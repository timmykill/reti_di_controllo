#include <stdlib.h>
#include <stdio.h>
#include "shared.h"



int die(char * msg, int ret)
{
	perror(msg);
	exit(ret);
	return 1;
}

/* testing utils */
int LOG_FD = 2;
#define NS_PER_SECOND 1000000000
void sub_timespec(struct timespec t1, struct timespec t2, struct timespec *td)
{
	td->tv_nsec = t2.tv_nsec - t1.tv_nsec;
	td->tv_sec	= t2.tv_sec - t1.tv_sec;
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

void save_time(struct timespec *t)
{
	clock_gettime(CLOCK_REALTIME, t);
}

void print_delta(struct timespec start, struct timespec finish)
{
	struct timespec delta;
	FILE* fp = fdopen(LOG_FD, "w");
	sub_timespec(start, finish, &delta);
	int i = fprintf(fp, "%d.%.9ld\n", (int)delta.tv_sec, delta.tv_nsec);
	i++;
}
#undef ND_PER_SECOND
