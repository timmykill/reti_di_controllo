#! /bin/bash
rm input_lock input_no_lock ordine_accesso_lock ordine_accesso_no_lock

for i in `seq 1 10`
do
echo $i >> input_lock
echo $i >> input_no_lock
done

for i in `seq 1 4`
do
printf "$i\ninput_lock\n" | ./client_lock localhost 50001 >> ordine_accesso_lock &
done

for i in `seq 1 4`
do
printf "$i\ninput_no_lock\n" | ./client_no_lock localhost 50001 >> ordine_accesso_no_lock &
done
