#! /bin/bash
echo "da scan.x genero stubs, poi compilo...";
rpcgen scan.x;
gcc scan_client.c scan_clnt.c scan_xdr.c -o client 2> /dev/null;
gcc scan_svc.c scan_proc.c scan_xdr.c -o server;
echo "-b per test buffer, -u per test udp"


while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    -u)
    {
    echo "inizializzo udp_error... ricorda di avere il portmapper avviato (rpcbind, una volta installato fai service rpcbind start)";
    cp scan_client.c udp_error/scan_client.c && cp scan_proc.c udp_error/scan_proc.c;
    cat scan.x | awk '{
    if (NR == 1) print "/*",$0;
    else if (NR == 2) print $0,"*/";
    else print $0
    }' > udp_error/scan.x;
    cd `pwd`/udp_error && chmod 0700 trova_udp_limite.sh && ./trova_udp_limite.sh && cd ..;}	    
    shift
    ;;
    -b)
    {
    echo "inizializzo best_buffer...risultati nei file di log";
    cp scan_client.c best_buffer/scan_client.c && cp scan_proc.c best_buffer/scan_proc.c && cp scan.x best_buffer/scan.x;
    cd `pwd`/best_buffer && chmod 0700 make_test.sh && ./make_test.sh && cd ..;}
    shift
    ;;
esac
done
