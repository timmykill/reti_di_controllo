
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>

#define LINE_LENGTH 256

int main(int argc, char **argv){

	struct hostent *host;
	struct sockaddr_in servaddr;
	int  port, sd, num1, num2, len, ris;
	char okstr[LINE_LENGTH];
	char c;

	/* CONTROLLO ARGOMENTI ---------------------------------- */
	if(argc != 3){
		printf("Error:%s serverAddress serverPort\n", argv[0]);
		exit(1);
	}

	        /* VERIFICA PORTA*/
        num1 = 0;
        while( argv[2][num1]!= '\0' ){
                if( (argv[2][num1] < '0') || (argv[2][num1] > '9') ){
                        printf("Secondo argomento non intero\n");
                        printf("Error:%s serverAddress serverPort\n", argv[0]);
                        exit(2);
                }
                num1++;
        }
        port = atoi(argv[2]); //porta server
        if (port < 1024 || port > 65535){
                printf("%s = porta scorretta...\n", argv[2]);
                exit(2);
        }

	host = gethostbyname(argv[1]);
        /* VERIFICA  HOST */
        if (host == NULL){
                printf("%s not found in /etc/hosts\n", argv[1]);
                exit(2);
        }


	/* INIZIALIZZAZIONE INDIRIZZO SERVER --------------------- */

	//indirizzo ip server
	memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr=((struct in_addr *)(host->h_addr))->s_addr;
	servaddr.sin_port = htons(port);

	/* CREAZIONE SOCKET ---------------------------------- */
	sd = socket(AF_INET, SOCK_DGRAM, 0); //creazione socket
	if(sd < 0){
		perror("apertura socket");
		exit(1);
	}
	printf("Client: creata la socket sd=%d\n", sd);

	/* CORPO DEL CLIENT: ciclo di accettazione di richieste da utente */
	printf("Inserire nome file: \n");
	while ((scanf("%s", okstr)) != EOF ){

		/* richiesta operazione */
		len = sizeof(servaddr);
		if(sendto(sd, &okstr, strlen(okstr), 0, (struct sockaddr *)&servaddr, len) < 0){
			perror("sendto");
			continue;
		}

		/* ricezione del risultato */
		printf("Attesa del risultato...\n");
		if (recvfrom(sd, &ris, sizeof(ris), 0, (struct sockaddr *)&servaddr, &len) < 0){
			perror("recvfrom");
			continue;
		}

		ris = ntohl(ris);
		printf("La line più lunga ha: %d caratteri\n", ris);

		printf("Inserire nome file: \n");

	} // while

	//CLEAN OUT
	close(sd);
	printf("\nClient: termino...\n");
	exit(0);
}
