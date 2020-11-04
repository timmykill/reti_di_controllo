#! /bin/bash

TEST_PORT=65111
LOC_TESTS="1000"
CLIENT_TESTS="1"
REMBUF="stdbuf -i0 -o0 -e0"
PAROLA="Antigone"


# Generate random files
#for loc in $LOC_TESTS; do
#	seq 1 100 | xargs -Inone cat /usr/share/dict/words | shuf | head -n $loc > files/$loc.txt
#done
for loc in $LOC_TESTS; do
	TEMPFILE=$(mktemp)
	for i in `seq 1000`; do
		cat /usr/share/dict/words >> $TEMPFILE
	done
	head -n $loc $TEMPFILE > files/$loc-orig.txt
	rm $TEMPFILE
done


for CCARGS in '' '-DREP_STR_MMAP' '-DREP_STR_READ'; do
	export CCARGS
	make purge > /dev/null 2>&1 
	make test > /dev/null 2> logs/compilation_$CCARGS.log
	if test $? -eq 0; then
		echo '[+] compilazione terminata con flag ' $CCARGS
	else
		echo '[-] qualcosa è andato storto, controlla il log di compilazione'
		echo 'waiting per continuare...'
		read 
	fi
	for loc in $LOC_TESTS; do
		for n_cli in $CLIENT_TESTS; do
			cp files/$loc-orig.txt files/$loc.txt
			#start server
			logprefix="logs/server-$CCARGS-$loc-$n_cli"
			$REMBUF ./server $TEST_PORT 2> $logprefix-timings.log > $logprefix-stdout.log & 
			echo $! > logs/server-$loc-$n_cli.pid 
			echo '[+] server in funzione' 
			
			# udp
			logprefix="logs/client_udp-$CCARGS-$loc-$n_cli"
			rm -f $logprefix-{timings,stdout}.log 
			for i in `seq 1 $n_cli`; do
				echo -e "files/$loc.txt\n$PAROLA\n" | $REMBUF ./client_udp localhost $TEST_PORT 2>> $logprefix-timings.log > $logprefix-stdout.log &
				echo $! > logs/client_udp-$loc-$n_cli-$i.pid 
			done
			echo '[+] started udp clients'

			for i in `seq 1 $n_cli`; do
				wait $(cat logs/client_udp-$loc-$n_cli-$i.pid)
				rm logs/client_udp-$loc-$n_cli-$i.pid
			done
			echo '[+] clients finished'
			
			#unit testing
			if ! sed "s/$PAROLA//g" files/$loc-orig.txt | diff -q files/$loc.txt - > /dev/null; then
				echo '[-] unit test fallito per loc:' $loc 'n_cli:' $n_cli 
				cat $logprefix-stdout.log
			fi
			#manca il test della risposta del server
			cat $logprefix-stdout.log | grep occorrenze
			
#			# tcp
#			logprefix="logs/client_tcp-$CCARGS-$loc-$n_cli"
#			rm -f $logprefix-{timings,stdout}.log 
#			for i in `seq 1 $n_cli`; do
#				echo -e "files/$loc.txt\n$PAROLA\n" | $REMBUF ./client_tcp localhost $TEST_PORT 2>> $logprefix-timings.log > $logprefix-stdout.log &
#				echo $! > logs/client_tcp-$loc-$n_cli-$i.pid 
#			done
#			echo '[+] started tcp clients'
#
#			for i in `seq 1 $n_cli`; do
#				wait $(cat logs/client_tcp-$loc-$n_cli-$i.pid)
#				rm logs/client_tcp-$loc-$n_cli-$i.pid
#			done
#			echo '[+] clients finished'
			
			# kill server
			kill -9 $(cat logs/server-$loc-$n_cli.pid) 
			echo '[+] killed server'
		done
	done
done
