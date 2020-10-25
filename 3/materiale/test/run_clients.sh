#! /bin/bash
#questo script lancia 5 client dandogli in input un file con 1 milione di righe
for i in `seq 1 5`; do {
	echo -e "files/f_100k_line\n" | ./client localhost 50000 & 
} done
	
