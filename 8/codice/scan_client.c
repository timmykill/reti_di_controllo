#include <stdio.h>
#include <errno.h>
#include "scan.h"
#ifdef DEBUG
#include "shared.h"
#endif
int main(int argc,char **argv){
	CLIENT *c;
	File_input f_in;
	File_res *f_out;
	Dir_input d_in;
	int *d_out;
	char tmp, path[MAXLENFILE];
	char *server;
	if(argc !=2){
		printf("bad arguments, usage: client server");
		exit(1);
	}
	server = argv[1];
	c = clnt_create(server, SCANPROG, SCANVERS, "udp");
	if (c == NULL){
		clnt_pcreateerror(server);
		exit(1);
	}
	do{
		printf("insert 'F' for file_scan or 'D' for dir_scan, then the file/directory path\n");
		if((tmp = getchar()) == EOF){
			break;
		}
		getchar();
		if(tmp != 'F' && tmp != 'D'){
			printf("not a valid char\n");
			continue;
		}
		printf("insert the path(max 2190 for files, 2186 for directories)\n");
		if(!(gets(path))){
			break;
		}
		if(tmp == 'F'){
			strcpy(f_in.file, path);
			#ifdef DEBUG
			struct timespec start, end;
			save_time(&start);
			#endif
			f_out = file_scan_1(&f_in,c);
			#ifdef DEBUG
			save_time(&end);
			print_delta(start, end);
			#endif
			if(f_out == NULL){
				clnt_perror(c, server); exit(1);
			}
			if(f_out->chars == -1){
				errno = f_out->lines;
				perror("errore in apertura file:\n");
				exit(1);
			}
			printf("chars: %d, words: %d, lines: %d\n",f_out->chars,f_out->words,f_out->lines);
		}else{
			printf("insert an int\n");
			scanf("%d",&(d_in.soglia));
			getchar();
			strcpy(d_in.dir, path);
			d_out = dir_scan_1(&d_in,c);
			if(d_out == NULL){
				clnt_perror(c, server); exit(1);
			}
			if((*d_out) == -1){
				printf("errore in ricerca directory\n");
				exit(1);
			}
			printf("%d files sopra soglia\n",(*d_out));
		}
	}while(TRUE);
	clnt_destroy(c);
	printf("See you...\n");
}

