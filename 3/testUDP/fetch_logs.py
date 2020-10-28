#! /usr/bin/env python

from os import listdir
from os.path import isfile, join
import re

logpath = "./logs/"

#fetch file di log
logfiles = [f for f in listdir(logpath) if isfile(join(logpath, f))]

#fetch client_parallel data
for tipo in ["client_parallel", "client_serial"]:
    client_parallel_logs = list(filter(lambda a : a.startswith(tipo), logfiles))
    lines = list(dict.fromkeys(map(lambda a: re.split("-|\.", a)[1], client_parallel_logs)))
    for l in lines:
        relevant_files = list(filter(lambda a : a.startswith(tipo + "-" + l + "-"), client_parallel_logs))
        n_clients = list(dict.fromkeys(map(lambda a: re.split("-|\.", a)[2], relevant_files)))
        for n in n_clients:
            filename = list(filter(lambda a : a.startswith(tipo + "-" + l + "-" + n + ".log"), client_parallel_logs))[0] 
            f = open(join(logpath, filename), "r")
            tempi = [float(a.strip()) for a in f.readlines()]
            print(tipo, "numero di righe:", l, "numero di client:", n)
            if len(tempi) == 0:
                print("no tempi disponibili")
            else:
                print("media:", sum(tempi) / len(tempi))
                print("min:", min(tempi))
                print("max:", max(tempi))


