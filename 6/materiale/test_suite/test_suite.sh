#! /bin/bash

CLIENT_TESTS="1 5 10"
REMBUF="stdbuf -i0 -o0 -e0"

# creazione file
cat /usr/share/dict/words > yolo

#compilare
javac *.java
rmic -vcompat Server
rmiregistry &
sleep 2

CCARG=1
loc=1
for n_cli in $CLIENT_TESTS; do
	#start server
	logprefix="logs/server-$CCARGS-$loc-$n_cli"
	$REMBUF java Server 2> $logprefix-timings.log > $logprefix-stdout.log & 
	sleep 2
	echo $! > logs/server-$loc-$n_cli.pid 
	echo '[+] server in funzione' $loc $n_cli $CCARGS
	
	logprefix="logs/client_udp-$CCARGS-$loc-$n_cli"
	for i in `seq 1 $n_cli`; do
		echo -e "C\nyolo\n1" | $REMBUF java Client localhost 2>> $logprefix-timings.log > $logprefix-$i-stdout.log &
		echo $! > logs/client_udp-$loc-$n_cli-$i.pid 
	done
	echo '[+] started udp clients'

	for i in `seq 1 $n_cli`; do
		wait $(cat logs/client_udp-$loc-$n_cli-$i.pid)
		rm logs/client_udp-$loc-$n_cli-$i.pid
	done
	echo '[+] clients finished'
	
	# kill server
	kill -9 $(cat logs/server-$loc-$n_cli.pid) 
	echo '[+] killed server'
done

killall rmiregistry
