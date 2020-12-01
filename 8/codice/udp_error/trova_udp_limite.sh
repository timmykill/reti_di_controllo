#! /bin/bash
MIN=2000;
MAX=2500;

echo "parto da $MIN fino a $MAX bytes, cerco il numero di bytes minimo che non entra più in un datagramma udp\nrisultato in max_byte_plus_one.txt";
for i in `seq $MIN $MAX`;
do
	rpcgen -DMAXLENFILE=$i -DMAXLENDIR=$i scan.x;
	gcc -D MAXLENFILE=$i scan_client.c scan_clnt.c scan_xdr.c -o client 2> /dev/null;
	gcc -D MAXLENDIR=$i scan_svc.c scan_proc.c scan_xdr.c -o server;
	echo "F\nscan.x\n\0" | ./client localhost 2>&1 >/dev/null | awk  '($4 != "encode") {exit 1}' && echo "$i" > max_byte_plus_one.txt && rm client s* && exit;
done;
