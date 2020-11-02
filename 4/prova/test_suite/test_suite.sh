#! /bin/bash

TEST_PORT=50000
LOC_TESTS="100000 1000000"
CLIENT_TESTS="10 30"


# Generate random files
#for loc in $LOC_TESTS; do
#	seq 1 100 | xargs -Inone cat /usr/share/dict/words | shuf | head -n $loc > files/$loc.txt
#done

for CCARGS in '-DDEL_OCC_MMAP' ''; do
	make purge
	CCARGS=$CCARGS make test
	for loc in $LOC_TESTS; do
		for n_cli in $CLIENT_TESTS; do
			#start server
			./server $TEST_PORT 2> logs/server-$loc-$n_cli.log & 
			echo $! > logs/server_serial-$loc-$n_cli.pid 
			
			# udp
			rm -f logs/client_udp-$loc-$n_cli.log
			for i in `seq 1 $n_cli`; do
				echo -e "files/$loc.txt\nyolo\n" | ./client_udp localhost $TEST_PORT 2>> logs/client_udp-$loc-$n_cli.log &
				echo $! > logs/client_udp-$loc-$n_cli-$i.pid 
			done
			for i in `seq 1 $n_cli`; do
				wait $(cat logs/client_udp-$loc-$n_cli-$i.pid)
				rm logs/client_udp-$loc-$n_cli-$i.pid
			done
			rm logs/client_udp-$loc-$n_cli.pid
			

			# kill server
			kill -9 $(cat logs/server_serial-$loc-$n_cli.pid)
		done
done
