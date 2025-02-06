#!/bin/bash

counter=0

while  [ $counter -lt 50 ]; do
    let counter=counter+1

    name=$(nl names.txt | grep -w $counter | awk '{print $2}' | awk -F ',' '{print $1}')
    lastname=$(nl names.txt | grep -w $counter | awk '{print $2}' | awk -F ',' '{print $2}')
    age=$(shuf -i 20-25 -n 1)

    mysql -u root -p{password} {database_name} -e "insert into {table_name} values ($counter, '$name', '$lastname', $age)"
    echo "$counter, $name, $lastname, $age was correctly imported"
done