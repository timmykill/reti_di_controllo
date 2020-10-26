CC = gcc
CFLAGS = -g -Wpedantic -Wall -Wextra
LINK = 
TARGETS = server_serial server_parallel client   
DOXYFILE = Doxyfile
shared_objects = shared.o

test: CFLAGS += -DTEST
test: $(TARGETS)

all: $(TARGETS)

$(TARGETS): %: $(shared_objects) %.o
	$(CC) $(CFLAGS) $(LINK) $(shared_objects) $@.o -o $@

%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm -f *.o

purge: clean
	rm $(TARGETS)
