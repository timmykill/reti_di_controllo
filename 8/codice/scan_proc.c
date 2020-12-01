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
File_res *file_scan_1_svc(File_input * input, struct svc_req * rq){
	static File_res result;
	int fd;
	char separators[] = {' ','\n'};
	char tmp;
	result.chars = 0;
	result.words = 0;
	result.lines = 0;

	if((fd = open(input->file,O_RDONLY)) == -1){
		result.chars = -1;
		result.lines = errno;
		return &result;
	}

	while(read(fd, &tmp,sizeof(char)) > 0){
		result.chars++;
	
		if(tmp == '\n'){
			result.lines++;
		}

		if(strchr(separators, tmp) != NULL){
			result.words++;
		}
		
	}
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
	return (&result);	
}
