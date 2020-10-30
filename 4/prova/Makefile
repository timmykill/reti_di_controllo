CC = gcc
LINK = 
CFLAGS = -g -Wpedantic -Wall -Wextra -flto
TARGETS = server client_tcp client_udp   
shared_objects = shared.o

test: CFLAGS += -DTEST
test: $(TARGETS)

all: $(TARGETS)

$(TARGETS): %: %.o $(shared_objects) 
	$(CC) $(CFLAGS) $(LINK) $(shared_objects) $< -o $@

%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm -f *.o

purge: clean
	rm $(TARGETS)
