#!/bin/bash

# Име на вашия файл
file="names.txt"

# Четене на само първите 15 реда от файла
head -n 15 "$file" | while read -r line; do
    # Извличане на първото и второто име използвайки разделител ','
    first_name=$(echo "$line" | cut -d ',' -f1)
    last_name=$(echo "$line" | cut -d ',' -f2)
    
    # Извеждане на първото и второто име
    echo "First Name: $first_name"
    echo "Last Name: $last_name"
    echo "---"  # Отделител
done

