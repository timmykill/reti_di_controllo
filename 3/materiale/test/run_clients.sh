#! /bin/bash

#lancia gli n clienti specificati tramite argomento dall'utente
[ $# -ne 1 ] && echo "Usage: $0 numeroClienti" && exit 1

for i in `seq 1 $1`; do {
	echo -e "files/f_1m_linee\n" | ./client localhost 50000 >> client.logs &
	
} done
	
