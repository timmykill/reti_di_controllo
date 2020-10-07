#include <sys/types.h>
#include <sys/wait.h>
#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#define MAX_STRING_LENGTH 256

// consumatore.c e' un filtro
int main(int argc, char* argv[]){
	int i,pid,figlio = 0;
	char *seq = argv[1];
	if(argc < 3){
		perror("numero argomenti sbagliato");
		exit(1);
	}
	for(i = 2;i < argc && !figlio;i++){
		pid = fork();
		if(pid < 0){
			printf("errore creazione figlio %d, uscita", (i-1));
			perror("");
			exit(2);
		}
		if(pid == 0){
			char tmp;
			int count = 0,fd;
			figlio = 1;
			fd = open(argv[i],O_RDWR);
			if(fd < 0){
				printf("errore apertura file %d",(i-1));
				perror("");
				exit(3);
			}
			while(read(fd,&tmp,sizeof(char)) > 0){
				if(strchr(seq, tmp) == NULL){/*se il ch non va eliminato e count>0 (quindi alcuni caratteri sono stati estromessi) scrivi il ch accanto all'ultimo carattere valido*/
					if(count != 0){
						lseek(fd,(-1*(count+1)),SEEK_CUR);
						write(fd,&tmp,sizeof(char));
						lseek(fd,count,SEEK_CUR);
						}
				}else{
					count++;
				}
			}
			if(ftruncate(fd,lseek(fd,0,SEEK_END)-count)<0){/*elimina lo schifo rimasto a dx che non serve più*/
				perror("errore durante resize file");
				exit(4);
			}
			close(fd);
		}
	}

	for(int r = 1;r < argc && !figlio;r++){
		wait(NULL);
	}
	return 0;
}
