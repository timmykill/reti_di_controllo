#include <stdlib.h>
#include <stdio.h>

int die(char * msg, int ret)
{
	perror(msg);
	exit(ret);
	return 1;
}
