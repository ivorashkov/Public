cut -d : -f 4 /data/colored-animals.txt | grep yellow | wc -l
cut -d - -f 2 /data/animals.txt | sort | uniq | wc -l
find / -user hero -type f | wc -l
find / -type f -name "*.basic" | wc -l
find / -type f -name "README" | wc -l
uname -r

cut -d ' ' -f 6,7 /data/fruits.txt | grep "love bananas" | wc -l
find / -type f -iname "*readme*" | wc -l
find / -type f -iname "README.*" | wc -l