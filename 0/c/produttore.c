#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#define MAX_STRING_LENGTH 256

int main(int argc, char* argv[]){
	int fd, readValues, bytes_to_write, written, i;
	char *file_out, *ret;
	char riga[MAX_STRING_LENGTH], buf[MAX_STRING_LENGTH];
	
	if (argc != 2){ 
		perror(" numero di argomenti sbagliato"); exit(1);
	} 
	
	file_out = argv[1];	
	
	fd = open(file_out, O_WRONLY|O_CREAT|O_TRUNC, 00640);
	if (fd < 0){
		perror("P0: Impossibile creare/aprire il file");
		exit(2);
	}
	do {
		printf("Inserisci la nuova riga\n");
		ret = gets(riga); 
		riga[strlen(riga)+1]='\0';  
		if(ret){
			riga[strlen(riga)]='\n';	
		}
		written = write(fd, riga, strlen(riga));
		if (written < 0){
			perror("P0: errore nella scrittura sul file");
			exit(3);
		}
	} while (ret);	
	close(fd);
}
