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

#define N 256
void gestore(int signo);

int main(int argc, char **argv){

	int sd, port, len, num1, ris = 0,pid,i = 0;
	char req[N], tmp;
	const int on = 1;
	struct sockaddr_in cliaddr, servaddr;
	struct hostent *clienthost;

	/* CONTROLLO ARGOMENTI ---------------------------------- */
	if(argc != 2){
		printf("Error: %s port\n", argv[0]);
		exit(1);
	}
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

    signal(SIGCHLD, gestore);
    
	/* CICLO DI RICEZIONE RICHIESTE OGNUNA GESTITA DA UN FIGLIO */
	for(;;){ //demone
		len = sizeof(struct sockaddr_in);
		if (recvfrom(sd, req, N, 0, (struct sockaddr *)&cliaddr, &len) < 0){
			perror("recvfrom ");
			continue;
		}
        if((pid=fork())==0){//codice di ciascun figlio
            
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
			while(read(fd, &tmp, 1) == 1){
				if((tmp != ' ') && (tmp != '\n')){
					i++;
				}else{
					if(ris < i){
						ris = i;
					}
				i = 0;
				}
			}
                }
            close(fd);
            ris = htonl(ris);//conversione risposta
            if (sendto(sd, &ris, sizeof(ris), 0, (struct sockaddr *)&cliaddr, len) < 0){
      			perror("sendto ");
                exit(1);
    		}
    		exit(0);//uscita con successo
        }//padre
		printf("Operazione gestita da %d e richiesta su file : %s\n",pid, req);
		
	} //for
}


void gestore(int signo){ 
    int stato;
    printf("esecuzione gestore di SIGCHLD\n");
    wait(&stato);
}
