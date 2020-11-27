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
File_res *file_scan_1_svc(char ** file, struct svc_req *){
	static File_res *result;
	int fd;
	char separators[] = {' ','\n', '\0' };
	char tmp;
	result->chars = 0;
	result->words = 0;
	result->lines = 0;

	if((fd = open(*file,O_RDONLY)) == -1){
		result->chars = -1;
		result->lines = errno;
		return result;
	}

	while(read(fd, &tmp,sizeof(char)) > 0){
		result->chars++;
	
		if(tmp == '\n'){
			result->lines++;
		}

		if(strchr(tmp) != NULL){
			result->words++;
		}
		
	}
	close(fd);
	return result;
}

int * dir_scan_1_svc(Dir_input * input, struct svc_req *){
	static int result = 0;
	DIR *dir;
	int fd;
	struct dirent *entry;
	char * path = (char*)malloc(strlen(input->dir)+257);
	
	if((dir = opendir(input->dir)) == NULL){
		result = -1;
		return (&result);
	}
	while((entry = readdir(dir)) != NULL){
		sprintf(path,"%s/%s",input->dir,entry->d_name);
		if(entry->d_type == DT_REG && (fd = open(path,O_RDONLY)) != -1){
			result = (lseek(fd,0,SEEK_END) > input->soglia) ? (result + 1) : result;
			close(fd);
		}
	}
	free(path);
	return (&result);	
}
