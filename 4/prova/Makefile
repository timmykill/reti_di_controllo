CC = gcc
LINK = 
CFLAGS = -g -Wpedantic -Wall -Wextra -Wno-unused-value -Wno-unused-parameter -O2 -Wno-unused-result 
DECLARED = -DMANUAL_READ_BUF_SIZE -DREAD_BUF_SIZE=16384 -DREP_STR_READ
TARGETS = server client_tcp client_udp   
shared_objects = shared.o
CFLAGS += ${CCARGS} ${DECLARED}

all: $(TARGETS)

$(TARGETS): %: %.o $(shared_objects) 
	$(CC) $(CFLAGS) $(LINK) $(shared_objects) $< -o $@

%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm -f *.o

purge: clean
	rm $(TARGETS)
