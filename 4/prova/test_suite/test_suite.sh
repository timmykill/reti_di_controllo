#! /bin/bash

TEST_PORT=65111
FILES_TESTS="500 1000 5000 10000" 
CLIENT_TESTS="1 5 10"
LOC_TESTS="100000000"
REMBUF="stdbuf -i0 -o0 -e0"
PAROLA="abaca"
TCP_DIR_TEST="files/tcp"


TEMPFILE=$(mktemp)
for i in `seq 15000`; do
	cat /usr/share/dict/words >> $TEMPFILE
done
for loc in $LOC_TESTS; do
	head -n $loc $TEMPFILE > files/$loc-orig.txt
done
rm $TEMPFILE

mkdir -p $TCP_DIR_TEST
for dir_i in $FILES_TESTS; do
	mkdir $TCP_DIR_TEST/$dir_i
	for i in `seq $dir_i`; do
		mkdir $TCP_DIR_TEST/$dir_i/$i
		for j in `seq 100`; do
			touch $TCP_DIR_TEST/$dir_i/$i/$j
		done
	done
done 2>/dev/null

# udp
for CCARGS in '-DREP_STR_MMAP' '-DREP_STR_READ' '-DREP_STR_READ -DMANUAL_READ_BUF_SIZE -DREAD_BUF_SIZE=8192' '-DREP_STR_READ -DMANUAL_READ_BUF_SIZE -DREAD_BUF_SIZE=16384' ''; do
	CCARGS="$CCARGS -DTEST" make purge > /dev/null 2>&1 
	CCARGS="$CCARGS -DTEST" make > /dev/null 2> logs/compilation_"$CCARGS".log
	if test $? -eq 0; then
		echo '[+] compilazione terminata con flag ' "$CCARGS"
	else
		echo '[-] qualcosa è andato storto, controlla il log di compilazione'
		echo 'waiting per continuare...'
		read 
	fi
	CCARGS=$(echo $CCARGS | tr ' ' '_')
	for loc in $LOC_TESTS; do
		for n_cli in $CLIENT_TESTS; do
			#start server
			logprefix="logs/server-$CCARGS-$loc-$n_cli"
			$REMBUF ./server $TEST_PORT 2> $logprefix-timings.log > $logprefix-stdout.log & 
			echo $! > logs/server-$loc-$n_cli.pid 
			echo '[+] server in funzione' $loc $n_cli $CCARGS
			
			logprefix="logs/client_udp-$CCARGS-$loc-$n_cli"
			rm -f $logprefix-{timings,stdout}.log 
			for i in `seq 1 $n_cli`; do
				cp files/$loc-orig.txt files/$loc-$i.txt
			done
			for i in `seq 1 $n_cli`; do
				echo -e "files/$loc-$i.txt\n$PAROLA\n" | $REMBUF ./client_udp localhost $TEST_PORT 2>> $logprefix-timings.log > $logprefix-$i-stdout.log &
				echo $! > logs/client_udp-$loc-$n_cli-$i.pid 
			done
			echo '[+] started udp clients'

			for i in `seq 1 $n_cli`; do
				wait $(cat logs/client_udp-$loc-$n_cli-$i.pid)
				rm logs/client_udp-$loc-$n_cli-$i.pid
			done
			echo '[+] clients finished'
			
			#unit testing
			if ! sed "s/$PAROLA//g" files/$loc-orig.txt | diff -q files/$loc-1.txt - > /dev/null; then
				echo '[-] unit test fallito per loc:' $loc 'n_cli:' $n_cli 
				cat $logprefix-stdout.log
			fi
			#manca il test della risposta del server
			cat $logprefix-stdout.log | grep occorrenze
			grep -c "$PAROLA" files/$loc-orig.txt
			
			# kill server
			kill -9 $(cat logs/server-$loc-$n_cli.pid) 
			echo '[+] killed server'
		done
	done
done

exit

#tcp
#for CCARGS in '-DSHOW_LV1_ENTRIES -DSHOW_LV1_DIR'; do
for CCARGS in ''; do
	CCARGS="$CCARGS -DTEST" make purge > /dev/null 2>&1 
	CCARGS="$CCARGS -DTEST" make > /dev/null 2> logs/compilation_"$CCARGS".log
	if test $? -eq 0; then
		echo '[+] compilazione terminata con flag ' "$CCARGS"
	else
		echo '[-] qualcosa è andato storto, controlla il log di compilazione'
		echo 'waiting per continuare...'
		read 
	fi
	#questa var ha un nome sbagliato per quello che rappresenta
	for files in $FILES_TESTS; do
		for n_cli in $CLIENT_TESTS; do
			cp files/$files-orig.txt files/$files.txt
			#start server
			logprefix="logs/server-$CCARGS-$files-$n_cli"
			$REMBUF ./server $TEST_PORT 2> $logprefix-timings.log > $logprefix-stdout.log & 
			echo $! > logs/server-$files-$n_cli.pid 
			echo '[+] server in funzione' $files $n_cli 
			
			logprefix="logs/client_tcp-$CCARGS-$files-$n_cli"
			rm -f $logprefix-{timings,stdout}.log 
			for i in `seq 1 $n_cli`; do
				echo -e "files/tcp/$files\n" | $REMBUF ./client_tcp localhost $TEST_PORT 2>> $logprefix-timings.log > $logprefix-$i-stdout.log &
				echo $! > logs/client_tcp-$files-$n_cli-$i.pid 
			done
			echo '[+] started tcp clients'

			for i in `seq 1 $n_cli`; do
				wait $(cat logs/client_tcp-$files-$n_cli-$i.pid)
				rm logs/client_tcp-$files-$n_cli-$i.pid
			done
			echo '[+] clients finished'
			
			# kill server
			kill -9 $(cat logs/server-$files-$n_cli.pid) 
			echo '[+] killed server'
		done
	done
done
