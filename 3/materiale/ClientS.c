/* Client per richiedere l'ordinamento remoto di un file */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/file.h>
#include <netinet/in.h>
#include <netdb.h>
#include <errno.h>

#define DIM_BUFF 256

int main(int argc, char *argv[])
{
	int sd, port, fdFile, nread, ok;
	char buff[DIM_BUFF], c;
	int numLinea, flockRes;
	char nomeFile[FILENAME_MAX + 1];
	struct hostent *host;
	struct sockaddr_in servaddr;


	/* CONTROLLO ARGOMENTI ---------------------------------- */
	if(argc!=3){
		printf("Error:%s serverAddress serverPort\n", argv[0]);
		exit(1);
	}

	/* INIZIALIZZAZIONE INDIRIZZO SERVER -------------------------- */
	memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
	servaddr.sin_family = AF_INET;
	host = gethostbyname(argv[1]);

	/*VERIFICA INTERO*/
	nread=0;
	while( argv[2][nread]!= '\0' ){
		if( (argv[2][nread] < '0') || (argv[2][nread] > '9') ){
			printf("Secondo argomento non intero\n");
			exit(2);
		}
		nread++;
	}
	port = atoi(argv[2]);

	/* VERIFICA PORT e HOST */
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


	/* CORPO DEL CLIENT:
	ciclo di accettazione di richieste da utente ------- */
	printf("Numero riga da eliminare prossimo file, EOF per terminare: ");

	while ((ok = scanf("%d", &numLinea)) != EOF){

		if( ok != 1){
			do {
				c = getchar();
			}while (c != '\n');
			printf("Numero riga da eliminare prossimo file, EOF per terminare: ");
			continue;
		}

		if(numLinea <= 0){
			perror("numLinea");
			printf("Inserire un intero maggiore di 0\n");
			printf("Numero riga da eliminare prossimo file, EOF per terminare: ");
		}else{
			printf("Inserire nome file: ");
			scanf("%s", nomeFile);
			printf("File da aprire: __%s__\n", nomeFile);

			/* Verifico l'esistenza del file */
			if((fdFile = open(nomeFile, O_RDONLY)) < 0){
				perror("file");
				printf("Il file non esiste\n");
				printf("Numero riga da eliminare prossimo file, EOF per terminare: ");
			}else{
                           
							/* CREAZIONE SOCKET ------------------------------------ */
							sd = socket(AF_INET, SOCK_STREAM, 0);
							if(sd < 0){
								perror("apertura socket");
								exit(1);
							}

							/* Operazione di BIND implicita nella connect */
							if(connect(sd,(struct sockaddr *) &servaddr, sizeof(struct sockaddr)) < 0){
								perror("connect");
								exit(1);
							}
							printf("Client: connect ok\n");

							numLinea = htons(numLinea);
							write(sd, &numLinea, sizeof(int));

							/*INVIO File*/
							printf("Client: invio file\n");
							while((nread = read(fdFile, buff, DIM_BUFF)) > 0){
								write(sd, buff, nread);	//invio
							}
							printf("Client: file inviato\n");
							/* Chiusura socket in spedizione -> invio dell'EOF */
							shutdown(sd,1);
                            
                            //chiusura file in lettura
                            close(fdFile);

							/*RICEZIONE File*/
							fdFile=open(nomeFile, O_WRONLY);
							printf("Client: ricevo e salvo file\n");
                            
                            //FLOCK PER MUTUA ESCLUSIONE 
                            while(flockRes=flock(fdFile,LOCK_EX)){
                                close(fdFile);
                                if(flockRes == EBADF){
                                    perror("fd is not an open file");
                                    exit(1);
                                }else if(flockRes == EWOULDBLOCK){
                                    perror("file attualmente bloccato da un altro utente");
                                }
                                printf("Sto aspettando");
                                fdFile=open(nomeFile, O_WRONLY);
                            }//all'uscita il file è bloccato
                                
                            printf("blocco\n");
                              
							while((nread = read(sd, buff, DIM_BUFF)) > 0){
								write(fdFile, buff, nread);
							}
							ftruncate(fdFile, lseek(fdFile, 0L, SEEK_CUR));
                            
                            while( flockRes = flock(fdFile, LOCK_UN));//unlock

							printf("Trasferimento terminato\n");
							/* Chiusura socket in ricezione */
							shutdown(sd, 0);
							/* Chiusura file */
							close(fdFile);
							close(sd);

							printf("Numero riga da eliminare prossimo file, EOF per terminare: ");
						}
			}

	}//while
	printf("\nClient: termino...\n");
	exit(0);
}
