#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdbool.h>
#include <string.h>
#define MAX_STRING_LENGTH 256

// consumatore.c e' un filtro
int main(int argc, char* argv[]){

	char *file_in, read_char, *filtered;
	int nread, fd, filterlen, i;
	bool is_filtered;

	if (argc == 3){
		file_in = argv[2];
		fd = open(file_in, O_RDONLY);
	} else if (argc == 2){
		fd = STDIN_FILENO
	} else {
		perror("numero argomenti sbagliato");
		exit(1);
	}
	filtered = argv[1];
	filterlen = strlen(filtered);

	while(nread = read(fd, &read_char, sizeof(char))) /* Fino ad EOF*/{
		if (nread >= 0){
			is_filtered = false;
			for (i = 0; i < filterlen; i++)
				is_filtered |= (read_char == filtered[i]);
			if (!is_filtered)
				putchar(read_char);
		} else {
			printf("(PID %d) impossibile leggere dal file %s", getpid(), file_in);
			perror("Errore!");
			close(fd);
			exit(3);
		}
	}
	close(fd);
}
