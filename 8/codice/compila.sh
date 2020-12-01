#! /bin/bash
echo "da scan.x genero stubs, poi compilo...";
rpcgen scan.x;
gcc scan_client.c scan_clnt.c scan_xdr.c -o client 2> /dev/null;
gcc scan_svc.c scan_proc.c scan_xdr.c -o server;
echo "inizializzo udp_error... ricorda di avere il portmapper avviato (rpcbind, una volta installato fai service rpcbind start)";
cp scan_client.c udp_error/scan_client.c && cp scan_proc.c udp_error/scan_proc.c;
cat scan.x | awk '{
if (NR == 1) print "/*",$0;
else if (NR == 2) print $0,"*/";
else print $0
}' > udp_error/scan.x;
cd `pwd`/udp_error && ./trova_udp_limite.sh && exit 0;
