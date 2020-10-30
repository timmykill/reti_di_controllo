#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <sys/types.h> 
#include <sys/wait.h>
#include <sys/select.h>

#include "shared.h"

#define N 256

int main(int argc, char **argv){

	int socket_udp, socket_tcp, port = 9999, queue_tcp;
	struct sockaddr_in cliaddr, servaddr;

	#ifdef TEST
	extern int LOG_FD;
	LOG_FD = 2;
	#endif

	memset ((char *)&servaddr, 0, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = INADDR_ANY;
	servaddr.sin_port = htons(port);

	socket_udp = socket(AF_INET, SOCK_DGRAM, 0);
	socket_tcp = socket(AF_INET, SOCK_STREAM, 0);

	/* sperando che compili */	
	socket_udp < 0 && socket_tcp < 0 && die("creazione socket", -1);

	bind(socket_udp ,(struct sockaddr *) &servaddr, sizeof(servaddr)) < 0 && die("bind udp", -2);
	bind(socket_tcp ,(struct sockaddr *) &servaddr, sizeof(servaddr)) < 0 && die("bind tcp", -3);

	listen(socket_tcp, queue_tcp) < 0 && die("listen tcp", -4);

	for(;;){
		puts("yolo\n");
	}
}
