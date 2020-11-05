#include <stdio.h>
#include <stdlib.h>
#include <sys/file.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

#include "shared.h"

#define BUFF_LEN 1024
int main(int argc, char *argv[])
{
	int fd_socket, port, nread, msg_len, msg_len_net;
	char buf[BUFF_LEN];
	struct hostent *host;
	struct sockaddr_in servaddr;
	#ifdef TEST
	struct timespec start, end;
	#endif

	if(argc!=3){
		printf("Usage: %s serverAddress serverPort\n", argv[0]);
		exit(1);
	}

	memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
	servaddr.sin_family = AF_INET;
	host = gethostbyname(argv[1]);

	nread=0;
	while(argv[2][nread] != '\0' ){
		if( (argv[2][nread] < '0') || (argv[2][nread] > '9') ){
			printf("Secondo argomento non intero\n");
			exit(2);
		}
		nread++;
	}
	port = atoi(argv[2]);

	if (port < 1024 || port > 65535){
		printf("%s = porta scorretta...\n", argv[2]);
		exit(2);
	}
	if (host == NULL){
		printf("%s not found in /etc/hosts\n", argv[1]);
		exit(2);
	}
	servaddr.sin_addr.s_addr=((struct in_addr *)(host->h_addr))->s_addr;
	servaddr.sin_port = htons(port);

	while (scanf("%s", buf)){
		#ifdef TEST
		save_time(&start);
		#endif
		(fd_socket = socket(AF_INET, SOCK_STREAM, 0)) < 0 && die("apertura socket", -100);
		connect(fd_socket,(struct sockaddr *) &servaddr, sizeof(struct sockaddr)) < 0 && die("connect", -101);

		msg_len = strlen(buf) + 1;
		msg_len_net = htonl(msg_len);
		write(fd_socket, &msg_len_net, sizeof(uint32_t));
		write(fd_socket, buf, msg_len);
		LOGD("SENDING msg: %s msg_len: %d\n", buf, msg_len);

		read(fd_socket, &msg_len_net, sizeof(uint32_t));
		msg_len = ntohl(msg_len_net);
		LOGD("RECIVED msg_len %d\n", msg_len);

		while (msg_len){
			read(fd_socket, buf, msg_len);
			printf("RECIVED %s\n", buf);
			read(fd_socket, &msg_len_net, sizeof(uint32_t));
			msg_len = ntohl(msg_len_net);
		}
		
		#ifdef TEST
		save_time(&end);
		print_delta(start, end);
		#endif
		
		LOGD("fine trasmissione\n");

		shutdown(fd_socket, 0);
		shutdown(fd_socket, 1);
		close(fd_socket);

		/* while testing scanf behaves weirdly, this helps */
		#ifdef TEST
		exit(1);	
		#endif
	}
}
