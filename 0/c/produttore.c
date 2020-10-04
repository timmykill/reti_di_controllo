#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#define MAX_STRING_LENGTH 256

int main(int argc, char* argv[]){
	int fd, written;
	char *ret;
	char riga[MAX_STRING_LENGTH];
	
	if (argc != 2){
		perror(" numero di argomenti sbagliato"); exit(1);
	}
	
	fd = open(argv[1], O_WRONLY|O_CREAT|O_TRUNC, 00640);
	if (fd < 0){
		perror("P0: Impossibile creare/aprire il file");
		exit(2);
	}
	do {
		printf("Inserisci la nuova riga\n");
		ret = gets(riga);
		if(ret){
			riga[strlen(riga)+1]='\0';
			riga[strlen(riga)]='\n';
	                written = write(fd, riga, strlen(riga));
	                if (written < 0){
	                        perror("P0: errore nella scrittura sul file");
	                        exit(3);
	                }
		}
	} while (ret);	
	close(fd);
}
