#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/param.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <sys/types.h> 
#include <sys/wait.h>
#include <sys/select.h>

#include "shared.h"

#define BUF_SIZE 256

int main(int argc, char **argv){

	int socket_udp, socket_tcp, port = 65111, queue_tcp = 100, nfds;
	struct sockaddr_in client_addr, server_addr;
	char buf[BUF_SIZE];
	size_t socklen_udp;
	const int on = 1;
	fd_set rset;

	#ifdef TEST
//	extern int LOG_FD;
//	LOG_FD = 2;
	#endif

	memset((char *) &server_addr, 0, sizeof(server_addr));
	server_addr.sin_family = AF_INET;
	server_addr.sin_addr.s_addr = INADDR_ANY;
	server_addr.sin_port = htons(port);

	socket_udp = socket(AF_INET, SOCK_DGRAM, 0);
	socket_tcp = socket(AF_INET, SOCK_STREAM, 0);

	(socket_udp < 0 || socket_tcp < 0) && die("creazione socket", -1);

	/* bind socket */
	setsockopt(socket_udp, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0 && die("setsockopt", -5);

	bind(socket_udp, (struct sockaddr *) &server_addr, sizeof(server_addr)) < 0 && die("bind udp", -2);
	bind(socket_tcp, (struct sockaddr *) &server_addr, sizeof(server_addr)) < 0 && die("bind tcp", -3);

	listen(socket_tcp, queue_tcp) < 0 && die("listen tcp", -4);

	/* set args select */
	FD_ZERO(&rset);
	FD_SET(socket_udp, &rset);
	FD_SET(socket_tcp, &rset);
	/* 
		MAX è una macro gcc
		/usr/include/sys/param.h
	*/
	nfds = MAX(socket_tcp, socket_udp) + 1;
	socklen_udp = sizeof(struct sockaddr_in);

	for(;;){
		select(nfds, &rset, NULL, NULL, NULL);
		if (FD_ISSET(socket_udp, &rset)){
//			recvfrom(socket_udp, buf, BUF_SIZE, 0, (struct sockaddr *) &client_addr, &socklen_udp);
//			printf("%s\n", buf);
			puts("udp\n");
		} else if (FD_ISSET(socket_tcp, &rset)){
			puts("tcp\n");
		}
	}
}
