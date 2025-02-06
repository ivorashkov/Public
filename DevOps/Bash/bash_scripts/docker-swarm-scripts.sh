#!/bin/bash

token=$(docker swarm join-token worker -q) #-> take the token for workers from manager      
ip=$( hostname -I | awk '{print $1}') #-> take IP address of the machine
hostname=$(hostname)

#Check if variables are null
if [[ -z $token ]]; then
        echo"Error: The token variable is empty or null"
        exit 1
fi

if [[ -z $ip ]]; then
        echo "Error: The IP address variable is empty or null"
        exit 1
fi

if [[ -z $hostname ]]; then
        echo "Error: The hostname variable is empty or null"
        exit 1
fi

echo "Storing hostname=$hostname"
echo "Storing host ip=$ip"
echo "Storing token=$token"

# Function to check if Docker is installed
function check_docker_installed {
    if ! command -v docker &> /dev/null
    then
        echo "Docker is not installed. Please install Docker and try again."
        exit 1
    fi
}

# Function to initialize Docker Swarm manager
function init_docker_swarm {
    echo "Initializing Docker Swarm manager..."

    # Check if the machine is already part of a swarm
    if docker info | grep -q "Swarm: active"; then
        echo "This node is already part of a Swarm."
    else
        # Initialize Docker Swarm manager
        docker swarm init --advertise-addr $ip

        if [ $? -eq 0 ]; then
            echo "Docker Swarm initialized successfully."
        else
            echo "Failed to initialize Docker Swarm."
            exit 1
        fi
    fi
}

function extract_worker_join_command {
echo "Join Manager with hostname $hostname"
join_token="docker swarm join --token $token $ip:2377"

echo "join string is >>> $join_token"
}

check_docker_installed
init_docker_swarm
extract_worker_join_command