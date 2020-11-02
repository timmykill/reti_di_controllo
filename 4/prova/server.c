#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/param.h>
#include <sys/stat.h>
#include <sys/mman.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <sys/types.h> 
#include <sys/wait.h>
#include <sys/select.h>

#include "shared.h"

#define BUF_SIZE 256

int deleteOccurences(char* file, char* word);
int replace_string_mmap(char* file, char* word);
void multiple_strstr(char * haystack, int haylen, char* needle, int needlen, int outfd, int * count);

int main(int argc, char **argv){

	int socket_udp, socket_tcp, port = 65111, queue_tcp = 100, nfds, ris;
	struct sockaddr_in client_addr, server_addr;
	char file[BUF_SIZE], word[BUF_SIZE];
	int socklen_udp;
	const int on = 1;
	fd_set rset;

	memset((char *) &server_addr, 0, sizeof(server_addr));
	server_addr.sin_family = AF_INET;
	server_addr.sin_addr.s_addr = INADDR_ANY;
	server_addr.sin_port = htons(port);

	socket_udp = socket(AF_INET, SOCK_DGRAM, 0);
	socket_tcp = socket(AF_INET, SOCK_STREAM, 0);

	(socket_udp < 0 || socket_tcp < 0) && die("creazione socket", -1);

	/* bind socket */
	setsockopt(socket_udp, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0 && die("setsockopt", -5);

	bind(socket_udp, (struct sockaddr *) &server_addr, sizeof(server_addr)) < 0 && die("bind udp", -2);
	bind(socket_tcp, (struct sockaddr *) &server_addr, sizeof(server_addr)) < 0 && die("bind tcp", -3);

	listen(socket_tcp, queue_tcp) < 0 && die("listen tcp", -4);

	/* set args select */
	FD_ZERO(&rset);
	FD_SET(socket_udp, &rset);
	FD_SET(socket_tcp, &rset);
	nfds = MAX(socket_tcp, socket_udp) + 1;
	socklen_udp = sizeof(struct sockaddr_in);
	
    printf("Server: mi metto in attesa\n");
    
	for(;;){
		select(nfds, &rset, NULL, NULL, NULL);//controllo necessario
		if (FD_ISSET(socket_udp, &rset)){
            recvfrom(socket_udp, file, BUF_SIZE, 0, (struct sockaddr *) &client_addr, &socklen_udp);//controllo
			recvfrom(socket_udp, word, BUF_SIZE, 0, (struct sockaddr *) &client_addr, &socklen_udp);
            //printf("%s\n",buffer);
            //char *file = strtok(buffer, " ");
            //char *word = strtok(NULL, " ");
            
            printf("Elimino occorrenze di %s da file %s\n",word,file);
            
			#if REP_STR_MMAP
			ris = replace_string_mmap(file, word);
			#else
            ris = deleteOccurences(file, word);
			#endif
            ris=htonl(ris);

            if(sendto(socket_udp,&ris,sizeof(ris),0, (struct sockaddr *) &client_addr, socklen_udp) < 0){
                perror("sendto");
                continue;
            }
			
		} else if (FD_ISSET(socket_tcp, &rset)){
			puts("tcp\n");
		}
	}
}

int deleteOccurences(char* file, char* word)
{
    int nread=0, i=0, found, j, k, stringLen, wordLen, numW=0, fd, fd_temp;
    char c;
    char buf[BUF_SIZE];
	fd=open(file,O_RDWR);
	fd_temp=open("filetemp",O_WRONLY|O_CREAT|O_TRUNC,0777);
	if(fd<0 || fd_temp<0){
		puts("something went wrong opening the file\n");
		return -1;
	}
    while((nread=read(fd,&c,sizeof(c)))>0){
        
        if(c!='\n'&&i<BUF_SIZE-1){
            buf[i]=c;
            i++;
            continue;
        }
        
        wordLen=strlen(word);
        stringLen=i;
        
         for(k=0; k <= stringLen - wordLen; k++){
            found = 1;
            for(j=0; j < wordLen; j++){
                if(buf[k + j] != word[j]){
                found = 0;
                break;
                }
            }
             if(found == 1){
                 numW++;
                for(j=k; j <= stringLen - wordLen; j++){
                    buf[j] = buf[j + wordLen];
                }

            stringLen = stringLen - wordLen;
            k--;
            }
         }
         
         write(fd_temp,buf,stringLen);//scrittura sul file temp della riga senza word
         i=0;//sovrascrizione buffer
        
        if(i>=BUF_SIZE-1){
            i=1;
            buf[0]=c;}
    }
    remove(file);
    rename("filetemp", file);
    return numW;
}

void multiple_strstr(char * haystack, int haylen, char* needle, int needlen, int outfd, int * count)
{
	char * start_strstr;
	start_strstr = strstr(haystack, needle);
	if (start_strstr){
		(*count)++;
		write(outfd, haystack, start_strstr - haystack);
		multiple_strstr(start_strstr + needlen, haylen - ((start_strstr - haystack) + needlen) ,needle, needlen, outfd, count);
	} else {
		write(outfd, haystack, haylen);
	}
}

 
int replace_string_mmap(char* file, char* word)
{
	int orig_fd, temp_fd;
	struct stat s;
	size_t size;
	char * mapped;
	char * temp_file = "tempfile";
	int count = 0;

	orig_fd = open(file, O_RDONLY);
	temp_fd = open(temp_file, O_WRONLY|O_CREAT|O_TRUNC, 0600);
	orig_fd < 0 && die("lettura file, open", -100);

	fstat(orig_fd, &s) < 0 && die("lettura file, fstat", -100);
	size = s.st_size;

	mapped = mmap (0, size, PROT_READ, MAP_PRIVATE, orig_fd, 0);
	mapped == MAP_FAILED && die("mmap", -100);

	multiple_strstr(mapped, size, word, strlen(word), temp_fd, &count);
    rename(temp_file, file);
	return count; 
}
