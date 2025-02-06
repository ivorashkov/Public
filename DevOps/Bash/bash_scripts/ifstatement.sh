# #!/bin/bash

# # echo "hello my name is Ivaylo"

#заместваме space със запетая:
#sed -i 's/ /,/g' your_file.txt

# # # ----------------------------
# # echo "print first param ${1}"
# # echo "print first param $1"
# # saved_param=$1
# # echo $saved_param
# # echo "saved param in text is ${saved_param}"
# # echo "saved param in text is $saved_param"

# # # ----------------------------
# # my_variable="Hello world"
# # echo "${my_variable}"
# # echo "$my_variable"
# # echo $my_variable
# # echo '$my_variable'
# # echo '${my_variable}'

# # # ----------------------------
# # declare -r color="red"
# # echo "read only variable ${color}"
# # declare -i number=6
# # echo "number variable ${number}"

# # # ----------------------------
# # # create function
# # fun(){
# # local local_var="100% visible ${1}"
# # echo $local_var
# # }

# # # calling the function to print the result
# # fun Ivaylo

# # # ----------------------------
# # print () {
# #     echo "print some text"
# # }

# # saved=$(print)
# # echo "print output is: ${saved}"
# # # ----------------------------
# # #see how we can print out and play out with the system values
# # date_output=$(date)
# # echo "current date and time: $date_output"
# # echo "current date and time: $(date)"
# # echo "current date and time:- "$(date)

# # #---------------------------------
# # #should be run as administrator
# # #check nginx status
# # # sudo systemctl status nginx | grep Loaded
# # # sudo systemctl status nginx | grep Active
# # nginx(){
# #     echo "Nginx status:"
# #     echo "$(sudo systemctl status nginx | grep -E 'Loaded|Active')" | sudo tee ./outputNginx.txt
# # }
# # # nginx

# # # sudo systemctl status nginx | awk '/Active:/ {print $2}' -> active

# # # getStatus=$(sudo systemctl status nginx | awk '/Active:/ {print $2}')
# # #             sudo service nginx status | awk '/Active:/ {print $2}'
# # getStatus=$(sudo systemctl is-active nginx)
# # echo $getStatus
# # stopOrStart(){
# #     if [ $getStatus == 'active' ]; then
# #         echo "status is $getStatus"
# #         sudo systemctl stop nginx
# #         echo "STOP NGINX -> $(sudo systemctl is-active nginx)"
# #     elif [ $getStatus == 'inactive' ]; then
# #         echo "status is $getStatus"
# #         sudo systemctl start nginx
# #         echo "START NGINX -> $(sudo systemctl is-active nginx)"
# #     fi
# # }
# # # stopOrStart

# # # sudo systemctl status nginx | grep -E 'Loaded|Active'
# # #sudo systemctl status nginx | grep active
# # #sudo nginx -t 
# # #nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
# # #nginx: configuration file /etc/nginx/nginx.conf test is successful

# # # -------------------------------
# # # param $2 sets the required version, if it is not set, the default version is automatically set to 19

# # setJava(){
# #     desired_version=${1:-19}
# #     if command -v java &> /dev/null; then
# #         echo "**************JAVACURRENT VERSION"
# #         current_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
# #         echo "Current Java version: $current_version !!"
# #         sudo java --version
# #     else
# #         echo "**************JAVA NOT INSTALLED"  
# #         current_version="0"
# #     fi

# #     if [ "$current_version" -eq "$desired_version" ]; then
# #         echo "**************JAVA ALREADY INSTALLED  $desired_version"
# #     else
# #         sudo apt-get purge openjdk* -y
# #         sudo ln -sf /usr/lib/jvm/java-1.7.0-openjdk-amd64/bin/java /usr/bin/java
# #         sudo apt update
# #         echo "****************STARTING INSTALLING JAVA VERSION ${desired_version}"
# #         sudo apt install openjdk-"$desired_version"-jre-headless -y

# #         if command -v java &> /dev/null; then
# #             installed_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
# #             if [ "$installed_version" -eq "$desired_version" ]; then
# #                 echo "********************************"
# #                 sudo java --version
# #             else
# #                 echo "Error: Failed to install Java $desired_version."
# #             fi
# #         else
# #             echo "Error: Java installation verification failed."
# #         fi
# #     fi
# # }

# # setJava 19



# paramIterator(){

#     if [ $# -eq 0 ]; then
#         echo "Usage: $0 <param1> <param2> ..."
#         echo "No params used"
#         exit 1
#     fi

#     initialFolder=$1
#     mkdir -p $initialFolder
#     echo "Create initialFolder folder with first param: $1"
#     shift 1
    
#     echo "Iterating through provided parameters:"
#     # iterate from first param
#     # for param in "$@"; do
#     #     echo "$param"
#     # done

#     # iterate from second param
#     echo "create sub folders:"
#     for i in "$@"; do
#         mkdir -p $initialFolder/$i
#         echo "Created subfolder $initialFolder/$i"

#         uuidParam=$(uuidgen -r)
#         echo "{\"name\":\"$i\",\"secret\":\"$uuidParam\"}" > $initialFolder/$i/secret.json
#         echo "Generated UUID and saved to $initialFolder/$i/secret.json"
#     done
# }

# paramIterator First second third fourth fiftx sixth


