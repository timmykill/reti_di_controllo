#! /bin/bash
TENTATIVI="5";
#con 50 si hanno circa 44 MB, raddoppiando FILE_DIM si raddoppiano i MB
FILE_DIM="100 1000 2000"
FILE="120_giornate.txt"
HOST="localhost";
#osservazione andamento a multipli del block size
BUF="2048 4096 6144 8192 10240 16384 22528 32768";
echo "misurazione tempi procedura file con buffer variabile";
rpcgen scan.x;
gcc -D DEBUG=1 shared.c scan_client.c scan_clnt.c scan_xdr.c -o client 2>/dev/null;
[ -f $FILE ] && rm $FILE;
rm *.log;
rm *.avg;
for buff in $BUF; #per differenti buffer
do
	gcc -D DEBUG=1 -D BUFFLEN=$buff scan_svc.c scan_proc.c scan_xdr.c -g -o server;
	for dim in $FILE_DIM; #per differenti dimensioni di file
	do
		for num in `seq 1 $dim`;
		do
			cat 120_giornate_di_sodoma.txt >> $FILE;
		done;	
		for i in `seq 1 $TENTATIVI`;#ripetilo n volte per poi poter fare la media
		do
			./server 0>/dev/null &
			echo $! > server-$buff.pid
			sleep 1
			printf "F\n$FILE\n\0" | ./client $HOST 2>&1>/dev/null | awk '{print}' >> log-$buff-$dim.log;
			kill -9 $(cat server-$buff.pid)
			rm server-$buff.pid
		done;
		rm $FILE
	done;
done;
for file in *.log;
do 
	cat $file | awk '{sum+=$1} END {print "AVG = ",sum/NR}' > $file.avg
done;
echo "fine test"
rm sc* client server 

