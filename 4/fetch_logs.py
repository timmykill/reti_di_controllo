#! /usr/bin/env python

from os import listdir
from os.path import isfile, join
import re

logpath = "./logs/"
define="nobuf"
blksize=""

#fetch file di log
logfiles = [f for f in listdir(logpath) if isfile(join(logpath, f))]
#fetch client_parallel data
print(define, blksize)
print("X,Y")
for tipo in ["client_udp"]:
    client_parallel_logs = list(filter(lambda a : a.startswith(tipo + "-" + define + "-" + blksize), logfiles))
    lines = list(dict.fromkeys(map(lambda a: re.split("-|\.", a)[3], client_parallel_logs)))
    for l in ['1000', '100000', '10000', '1000000']:
        relevant_files = list(filter(lambda a : a.startswith(tipo + "-" + define + "-" + blksize + "-" + l + "-"), client_parallel_logs))
        n_clients = list(dict.fromkeys(map(lambda a: re.split("-|\.", a)[4], relevant_files)))
        for n in ["100"]:
            #client_tcp--500-1-timings.log
            filename = list(filter(lambda a : a.startswith(tipo + "-" + define + "-" + blksize + "-" + l + "-" + n + "-timings.log"), client_parallel_logs))[0] 
            f = open(join(logpath, filename), "r")
            tempi = [float(a.strip()) for a in f.readlines()]
            if len(tempi) == 0:
                print("no tempi disponibili")
            else:
                #print("media:", sum(tempi) / len(tempi))
                print(l + "," + str(max(tempi)))
                #print()
