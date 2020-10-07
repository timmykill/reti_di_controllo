
#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#define MAX_STRING_LENGTH 256

int main(int argc, char* argv[]){
        char line[MAX_STRING_LENGTH], *ret;
        int r,malformata=0,linelen,*fd, file_to_write,num_file = argc-1;

	if (argc == 1){
		perror("numero argomenti sbagliato");
		exit(1);
	}
	if((fd = malloc(sizeof(int)*(argc-1))) == NULL){
		printf("errore in allocazione memoria");
		exit(2);
	}
	
	for(int i = 1;i <= num_file;i++){
		fd[i-1] = open(argv[i], O_WRONLY|O_CREAT|O_TRUNC, 00640);
		if(fd[i-1] < 0){
			free(fd);
			perror("errore in apertura file");
			exit(3);
		}
	}

	do{
		printf("inserisci una nuova riga\n");
		if((ret = gets(line))){
			linelen=strlen(line);
			line[linelen + 1] = '\0';
			line[linelen] = '\n';
			if(line[0] < 49 || line[0] > 57){ /*controllo che il primo numero non sia 0*/
				malformata++;
			}
			for(r = 0;line[r] != ':' && !malformata && r < linelen;r++){ /*controllo che prima di : ci siano interi*/
				if(line[r] < 48 || line[r] > 57){
					malformata++;
				}
			}
			if(malformata || r == linelen || (file_to_write = atoi(line)) > num_file){/*controllo se uno dei precedenti controlli ha trovato irregolarità o se la riga non ha :*/
				malformata = 0;
				printf("riga malformata, riprova\n");
			}else{
				line[r] = '\0';/*sostituisco a : \0 per poter dare in pasto ad atoi solo gli interi*/
				if(write(fd[file_to_write-1],line + r + 1,linelen - ( r + 1)) < 0){/*sul file indicato dall'utente (-1 per via della rappresentazione ad array dei file) scrivo da linea+r+1 (inizio della riga vera e propria) */
					perror("P0: errore nella scrittura sul file");
					free(fd);
		        		exit(4);
				}
			}
		}
	}while(ret);

	for(int i = 0;i < num_file;i++){
		close(fd[i]);
	}
	free(fd);
	return 0;
}
