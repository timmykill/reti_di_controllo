
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

#define N 256
int searchWord(int fd);

int main(int argc, char **argv){

	int sd, port, len, num1, ris;
  char req[N];
	const int on = 1;
	struct sockaddr_in cliaddr, servaddr;
	struct hostent *clienthost;

	/* CONTROLLO ARGOMENTI ---------------------------------- */
	if(argc != 2){
		printf("Error: %s port\n", argv[0]);
		exit(1);
	}
	else{
		num1 = 0;
		while(argv[1][num1] != '\0' ){
			if((argv[1][num1] < '0') || (argv[1][num1] > '9')){
				printf("Secondo argomento non intero\n");
				printf("Error: %s port\n", argv[0]);
				exit(1);
			}
			num1++;
		}
	  	port = atoi(argv[1]);
  		if (port < 1024 || port > 65535){
		      printf("Error: %s port\n", argv[0]);
		      printf("1024 <= port <= 65535\n");
		      exit(1);
  		}
	}

	/* INIZIALIZZAZIONE INDIRIZZO SERVER ---------------------------------- */
	memset ((char *)&servaddr, 0, sizeof(servaddr)); //copia 0 in serveraddr per una lunghezza paria a servaddr
	servaddr.sin_family = AF_INET; //sempre AF_INET famiglia internet
	servaddr.sin_addr.s_addr = INADDR_ANY;  //qualunque indirizzo
	servaddr.sin_port = htons(port);  //htons = host to network per risolvere per decodifica interi

	/* CREAZIONE, SETAGGIO OPZIONI E CONNESSIONE SOCKET -------------------- */
	sd = socket(AF_INET, SOCK_DGRAM, 0); //socket(dominio, tipo, protocollo) AF_INET famiglia internet,
                                       //SOCK_DGRAM socket datagram e protocollo 0 = protocollo di trasporto
	if(sd < 0){
    perror("creazione socket "); //errore creazione socket
    exit(1);
  }
	printf("Server: creata la socket, sd=%d\n", sd);

	if(setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0){
    perror("set opzioni socket ");
    exit(1);
  }
	printf("Server: set opzioni socket ok\n");

	if(bind(sd,(struct sockaddr *) &servaddr, sizeof(servaddr)) < 0){
    perror("bind socket ");
    exit(1);
  }
	printf("Server: bind socket ok\n");

	/* CICLO DI RICEZIONE RICHIESTE ------------------------------------------ */
	for(;;){ //demone
		len = sizeof(struct sockaddr_in);
		if (recvfrom(sd, req, N, 0, (struct sockaddr *)&cliaddr, &len) < 0){
      perror("recvfrom ");
      continue;
    }

		printf("Operazione richiesta su file: %s\n", req);
		clienthost = gethostbyaddr( (char *) &cliaddr.sin_addr, sizeof(cliaddr.sin_addr), AF_INET);
		if (clienthost == NULL){
      printf("client host information not found\n");
    }else{
      printf("Operazione richiesta da: %s %i\n", clienthost->h_name,(unsigned)ntohs(cliaddr.sin_port));
    }

		/*EXEC*/
		int fd = open(req, O_RDONLY, 0777);
    if(fd < 0){
      printf("Errore apertura file\n");
      ris = -1;
    }else{
      ris = searchWord(fd);
    }

		ris = htonl(ris);//conversione risposta
		if (sendto(sd, &ris, sizeof(ris), 0, (struct sockaddr *)&cliaddr, len) < 0){
      perror("sendto ");
      continue;
    }
	} //for
}

int searchWord(int fd){
  char tmp;
  int i = 0, result = 0;

  while(read(fd, &tmp, 1) == 1){
    if((tmp != ' ') && (tmp != '\n')){
      i++;
    }else{
      if(result < i){
        result = i;
      }
      i = 0;
    }
  }

  return result;

}
