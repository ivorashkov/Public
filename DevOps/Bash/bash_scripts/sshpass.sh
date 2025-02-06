#!/bin/bash

for user in ansible root
do
    for os in ubuntu centos
    do
        for instance in 1 2 3
        do
            sshpass -f password.txt ssh-copy-id -o StrictHostKeyChecking=no ${user}@${os}${instance}
        done
    done
done