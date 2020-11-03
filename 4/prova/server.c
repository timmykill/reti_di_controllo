#include <stdio.h>
#include <dirent.h>
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
inline int replace_string_mmap(char* file, char* word) __attribute__((always_inline));
void multiple_strstr(char * haystack, int haylen, char* needle, int needlen, int outfd, int * count);

int main(int argc, char **argv){

	int socket_udp, socket_tcp, port = 65111, queue_tcp = 100, nfds, ris;
	struct sockaddr_in client_addr, server_addr;
	char file[BUF_SIZE], word[BUF_SIZE];
	unsigned int client_addr_len;
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
	setsockopt(socket_udp, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0 && die("setsockopt udp", -5);
	setsockopt(socket_tcp, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0 && die("setsockopt tcp", -5);

	bind(socket_udp, (struct sockaddr *) &server_addr, sizeof(server_addr)) < 0 && die("bind udp", -2);
	bind(socket_tcp, (struct sockaddr *) &server_addr, sizeof(server_addr)) < 0 && die("bind tcp", -3);

	listen(socket_tcp, queue_tcp) < 0 && die("listen tcp", -4);

	/* set args select */
	FD_ZERO(&rset);
	FD_SET(socket_udp, &rset);
	FD_SET(socket_tcp, &rset);
	nfds = MAX(socket_tcp, socket_udp) + 1;
	client_addr_len = sizeof(client_addr);
	
	printf("Server: mi metto in attesa\n");
    
	for(;;){
		LOGD("waiting for select\n");
		select(nfds, &rset, NULL, NULL, NULL);
		LOGD("select returned\n");
		if (FD_ISSET(socket_udp, &rset)){
			LOGD("udp is set\n");
			recvfrom(socket_udp, file, BUF_SIZE, 0, (struct sockaddr *) &client_addr, &client_addr_len);
			recvfrom(socket_udp, word, BUF_SIZE, 0, (struct sockaddr *) &client_addr, &client_addr_len);
			printf("Elimino occorrenze di %s da file %s\n",word,file);

			#if REP_STR_MMAP
			ris = replace_string_mmap(file, word);
			#else
			ris = deleteOccurences(file, word);
			#endif
			ris=htonl(ris);

			if(sendto(socket_udp, &ris, sizeof(ris), 0, (struct sockaddr *) &client_addr, client_addr_len) < 0){
				perror("sendto");
				continue;
			}
		}
		if (FD_ISSET(socket_tcp, &rset)){
			int socket_conn;
			LOGD("tcp is set\n");
			printf("%d\n", socket_tcp);
			if ((socket_conn = accept(socket_tcp, (struct sockaddr *) &client_addr, &client_addr_len)) < 0){
				if (errno == EINTR){
					perror("Forzo la continuazione della accept");
					continue;
				} else {
        			 die("socket tcp", -200);
      				}
			}
			if (fork() == 0){
				uint32_t msg_len, msg_len_net;
				size_t tmp_sizet;
				char msg[BUF_SIZE];
				DIR * dir1, * dir2;
				char * entry1_name, * entry2_name;
				struct dirent * entry1, * entry2;
				struct hostent * host;
				const int zero = 0;

				close(socket_tcp);
				host = gethostbyaddr((char *) &client_addr.sin_addr, sizeof(client_addr.sin_addr), AF_INET);
				if (host == NULL) {
					printf("client host information not found\n");
					continue;
				} else {
					printf("Server (figlio): host client e' %s \n", host->h_name);
				}
				LOGD("child starting\n");
				read(socket_conn, &msg_len_net, sizeof(uint32_t));
				msg_len = ntohl(msg_len_net);
				LOGD("msg_len: %d\n", msg_len);
				read(socket_conn, msg, msg_len);
				LOGD("msg: %s\n", msg);
				
				dir1 = opendir(msg);
				/* we ar gonna need this in lv2 */
				tmp_sizet = msg_len;
				if (dir1) {
					while ((entry1 = readdir(dir1))) {
						/* inlined strcmp */
						entry1_name = entry1->d_name;
						if (entry1_name[0] == '.' && (entry1_name[1] == '\0' || (entry1_name[1] == '.' && entry1_name[2] == '\0')))
							continue;
						LOGD("--> %s\n", entry1_name);
						if (entry1->d_type == DT_DIR){
							/*
							 * reusing the buffer and buffer len since its not used anymore
							 * this might be a very bad idea, but it saves on heap allocation
							 */
							msg_len = strlen(entry1_name) + strlen(msg) + 1;
							if (msg_len > BUF_SIZE){
								LOGD("Implement heap allocation or increase BUF_SIZE\n");
								continue;
							}

							LOGD("%d\n", tmp_sizet);
							msg_len = strlen(entry1_name) + 1;
							/* keeping \0 */
							memcpy(msg + tmp_sizet, entry1_name, msg_len);
							#ifdef SHOW_LV1_DIR
							msg_len_net = htonl(msg_len);
							LOGD("SENDING msg: %s, msg_len: %d\n", entry1_name, msg_len);
							write(socket_conn, &msg_len_net, sizeof(uint32_t));
							write(socket_conn, entry1_name, msg_len);
							#endif
							msg[tmp_sizet - 1] = '/';
							LOGD("dir: %s\n", msg);
							dir2 = opendir(msg);
							if (dir2) {
								while ((entry2 = readdir(dir2))) {
									/* inlined strcmp */
									entry2_name = entry2->d_name;
									if (entry2_name[0] == '.' && (entry2_name[1] == '\0' || (entry2_name[1] == '.' && entry2_name[2] == '\0')))
										continue;
									LOGD("-->--> %s\n", entry2_name);
									msg_len = strlen(entry2_name) + 1;
									msg_len_net = htonl(msg_len); 
									write(socket_conn, &msg_len_net, sizeof(uint32_t));
									write(socket_conn, entry2_name, msg_len);
								}
							} else {
								LOGD("non ho potuto aprire la dir lv2\n");
							}
						#ifdef SHOW_LV1_ENTRIES
						} else {
							msg_len = strlen(entry1_name) + 1;
							msg_len_net = htonl(msg_len); 
							write(socket_conn, &msg_len_net, sizeof(uint32_t));
							write(socket_conn, entry1_name, msg_len);
						}
						#else
						}
						#endif
					}
					closedir(dir1);
				} else {
					LOGD("non ho potuto aprire la dir\n");
				}
				
				LOGD("child ha finito1\n");
				/* fine conversazione ?ridondante?*/
				write(socket_conn, &zero, sizeof(uint32_t));
				LOGD("child ha finito2\n");
				shutdown(socket_conn, 0);
				shutdown(socket_conn, 1);
				close(socket_conn);
				exit(1);
			}
		}
		FD_ZERO(&rset);
		FD_SET(socket_udp, &rset);
		FD_SET(socket_tcp, &rset);
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
        
        if(i<BUF_SIZE-1){
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
	close(fd);
	close(fd_temp);
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

 
inline int replace_string_mmap(char* file, char* word)
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

	mapped = mmap(0, size, PROT_READ, MAP_PRIVATE | MAP_POPULATE, orig_fd, 0);
	mapped == MAP_FAILED && die("mmap", -100);

	multiple_strstr(mapped, size, word, strlen(word), temp_fd, &count);
	
	munmap(mapped, size);
	close(orig_fd);
	close(temp_fd);
    rename(temp_file, file);
	return count; 
}

inline int replace_string_read(char* file, char* word)
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

	mapped = mmap(0, size, PROT_READ, MAP_PRIVATE | MAP_POPULATE, orig_fd, 0);
	mapped == MAP_FAILED && die("mmap", -100);

	multiple_strstr(mapped, size, word, strlen(word), temp_fd, &count);
	
	munmap(mapped, size);
	close(orig_fd);
	close(temp_fd);
	rename(temp_file, file);
	return count; 
}
