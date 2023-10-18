#!/bin/bash

# Navigate to the directory
#cd .

if [ -d "WebApp" ]; then
    echo "Removing old Cloud directory..."
    rm -rf WebApp
fi

# Unzip the application
echo "Unzipping cloudapplication.zip..."
#unzip Pranav_Khismatrao_00276375_03.zip
unzip WebApp.zip

ls

#echo "Go to POM DIRECTORY"
#cd ./WebApp/CloudAssignment03 || exit

echo "Current directory"
pwd

# Uncomment the next line if you need to compile and package using Maven on your Debian server
# mvn clean package

# Navigate to the target directory
cd /tmp/CloudAssignment03-0.0.1-SNAPSHOT.jar || exit


# Run the Spring Boot application with the prodsuction profile
java -jar CloudAssignment03-0.0.1-SNAPSHOT.jar --spring.profiles.active=production

