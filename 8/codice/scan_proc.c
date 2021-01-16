#include "scan.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <dirent.h>
#include <errno.h>
#include <string.h>
#include <stdio.h>

#ifndef DEBUG
#define BUFFLEN 16384
#endif
File_res *file_scan_1_svc(File_input * input, struct svc_req * rq){
	static File_res result;
	int fd, nread;
	char separators[] = {' ', '\n', '.', ',','\0'};
	char buff[BUFFLEN];
	result.chars = 0;
	result.words = 0;
	result.lines = 0;
	printf("richiesta accettata...\n");
	if((fd = open(input->file,O_RDONLY)) == -1){
		result.chars = -1;
		result.lines = errno;
		return &result;
	}

	while((nread = read(fd, buff, BUFFLEN)) > 0){
		for(int i = 0; i < nread; i++){
			char tmp = buff[i];
			result.words = (strchr(separators, tmp) != NULL)?(result.words + 1):result.words;
			result.chars++;
			result.lines = (buff[i] == '\n')?(result.lines + 1):result.lines;

		}
	}
	printf("...richiesta servita\n");
	close(fd);
	return &result;
}

int * dir_scan_1_svc(Dir_input * input, struct svc_req * rq){
	static int result;
	result = 0;
	DIR *dir;
	int fd;
	struct dirent *entry;
	char path[MAXLENDIR+257];//alloco abbastanza spazio per contenere il path per dir + / + nome file(max 256)
	printf("richiesta su directory ricevuta...\n");
	if((dir = opendir(input->dir)) == NULL){
		result = -1;
		return (&result);
	}

	while((entry = readdir(dir)) != NULL){//per ogni entry nella dir controllo che sia un file, lo apro e controllo che superi la soglia
		if((entry->d_type == DT_REG) && sprintf(path,"%s/%s",input->dir,entry->d_name) && (fd = open(path,O_RDONLY)) != -1){
			result = (lseek(fd,0,SEEK_END) > input->soglia) ? (result + 1) : result;
			close(fd);
		}
	}
	printf("...richiesta su directory servita\n");
	return (&result);	
}
