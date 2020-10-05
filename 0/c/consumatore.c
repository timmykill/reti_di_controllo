#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#define MAX_STRING_LENGTH 256

//https://en.wikipedia.org/wiki/Tail_call#C_example
void multiple_strstr(char * haystack, int haylen, char* needle, int needlen, int outfd){
	char * start_strstr;
	start_strstr = strstr(haystack, needle);
	if (start_strstr){
		write(outfd, haystack, start_strstr - haystack);
		multiple_strstr(start_strstr + needlen, haylen - ((start_strstr - haystack) + needlen) ,needle, needlen, outfd);
	} else {
		write(outfd, haystack, haylen);
	}
}

int main(int argc, char* argv[]){
	char *file_in, read_char, buf[MAX_STRING_LENGTH], *needle, *start_strstr;
	int nread, fd, i, outfd;
	
	if(argc == 3){
		file_in = argv[2];
		fd = open(file_in, O_RDONLY);
	}else if(argc == 2){
		fd = 0;
	}else{
		perror("numero argomenti sbagliato");
		exit(1);
	}

	needle = argv[1];
	outfd = 1;
	if (fd<0){
		perror("P0: Impossibile aprire il file.");
		exit(2);
	}

	for(i = 0; nread = read(fd, &read_char, sizeof(char)); i++){
		if (nread >= 0){
			buf[i] = read_char;
			if (read_char == '\n'){
				buf[i+1] = '\0';
				multiple_strstr(buf, strlen(buf), needle, strlen(needle), outfd);
				i=-1;
			}
		} else {
			printf("(PID %d) impossibile leggere dal file %s", getpid(), file_in);
			perror("Errore!");
			close(fd);
			exit(3);
		}
	}

	close(fd);
}
