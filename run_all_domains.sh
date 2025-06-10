#!/bin/bash

# List of domains to process
DOMAINS=(
    "blocks-world"
    "campus"
    "depots"
    "driverlog"
    "dwr"
    "easy-ipc-grid"
    "ferry"
    "intrusion-detection"
    "kitchen"
    "logistics"
    "miconic"
    "rovers"
    "satellite"
    "sokoban"
    "zeno-travel"
)

# Function to check if screen session exists
screen_exists() {
    screen -list | grep -q "$1"
    return $?
}

# Create screen sessions for each domain
for domain in "${DOMAINS[@]}"; do
    # Create a screen session name based on the domain
    screen_name="goalrec_${domain}"
    
    # Check if screen session already exists
    if screen_exists "$screen_name"; then
        echo "Screen session $screen_name already exists. Skipping."
        continue
    fi
    
    # Create a new detached screen session for this domain
    echo "Starting screen session for domain: $domain"
    screen -dmS "$screen_name" bash -c "cd $(pwd) && python3 run_goalrecognizer.py $domain; exec bash"
    
    # Wait a bit before starting the next screen to avoid resource contention
    sleep 2
done

echo "All screen sessions started. Use 'screen -ls' to see running sessions."
echo "To attach to a session, use 'screen -r goalrec_domain_name'"
echo "To detach from a session, press Ctrl+A, then D"