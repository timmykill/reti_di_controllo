#! /bin/bash

TEST_PORT=50000
LOC_TESTS="100000 1000000"
CLIENT_TESTS="10 30"

# Compilation using test flag
make test

# Generate random files
for loc in $LOC_TESTS; do
	seq 1 100 | xargs -Inone cat /usr/share/dict/words | shuf | head -n $loc > files/$loc.txt
done


for loc in $LOC_TESTS; do
	for n_cli in $CLIENT_TESTS; do
		# serial
		./server_serial $TEST_PORT 2> logs/server_serial-$loc-$n_cli.log & 
		echo $! > logs/server_serial-$loc-$n_cli.pid 
		rm -f logs/client_serial-$loc-$n_cli.log
		for i in `seq 1 $n_cli`; do
			echo -e "files/$loc.txt\n" | ./client localhost $TEST_PORT >> logs/client_serial-$loc-$n_cli.log &
			echo $! > logs/client_serial-$loc-$n_cli-$i.pid 
		done
		for i in `seq 1 $n_cli`; do
			wait $(cat logs/client_serial-$loc-$n_cli-$i.pid)
			rm logs/client_serial-$loc-$n_cli-$i.pid
		done
		kill -9 $(cat logs/server_serial-$loc-$n_cli.pid)
		rm logs/server_serial-$loc-$n_cli.pid

		# parallel
		./server_parallel $TEST_PORT 2> logs/server_parallel-$loc-$n_cli.log & 
		echo $! > logs/server_parallel-$loc-$n_cli.pid 
		rm -f logs/client_parallel-$loc-$n_cli.log
		for i in `seq 1 $n_cli`; do
			echo -e "files/$loc.txt\n" | ./client localhost $TEST_PORT >> logs/client_parallel-$loc-$n_cli.log &
			echo $! > logs/client_parallel-$loc-$n_cli-$i.pid 
		done
		for i in `seq 1 $n_cli`; do
			wait $(cat logs/client_parallel-$loc-$n_cli-$i.pid)
			rm logs/client_parallel-$loc-$n_cli-$i.pid
		done
		kill -9 $(cat logs/server_parallel-$loc-$n_cli.pid)
		rm logs/server_parallel-$loc-$n_cli.pid
	done
done
	
