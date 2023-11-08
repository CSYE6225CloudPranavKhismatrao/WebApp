#!/bin/bash
# ======================================= Update and Upgrade =======================================================
echo "---x---Adding Java Repository---x---"
sudo apt update && sudo apt upgrade -y

#=================================== Installing Java 17===================================================================================================
echo "---x---Installing Java 17---x---"
sudo apt install openjdk-17-jdk -y

#=================================== Installing Jenkins===================================================================================================
echo "export CSV_PATH=/opt/users.csv" >> ~/.bashrc

echo "---x---Setting Java Environment Variables---x---"
echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64/" >> ~/.bashrc
echo "export PATH=$PATH:$JAVA_HOME/bin" >> ~/.bashrc

#=================================== Reload bashrc ===================================================================================================
source ~/.bashrc

# ======================================= Update and Upgrade MAVEN =======================================================
echo "---x---Installing Maven---x---"
sudo apt install maven -y


#=================================== Installing Cloudwatch Agent===================================================================================================
sudo wget https://amazoncloudwatch-agent.s3.amazonaws.com/debian/amd64/latest/amazon-cloudwatch-agent.deb
sudo dpkg -i -E ./amazon-cloudwatch-agent.deb
#===================================================================================================================================================================

#echo "---x---Installing PostgreSQL---x---"
#sudo apt install postgresql postgresql-contrib -y
#
## Start and enable PostgreSQL to start on boot
#sudo systemctl start postgresql
#sudo systemctl enable postgresql

# Create a PostgreSQL database

#export CSV_PATH=/opt/users.csv

## Configure PostgreSQL: set password, create database, and create user
#sudo -u postgres createuser  myuser  # --noninteractive --pwprompt
# sudo -u postgres psql -c "ALTER USER myuser WITH PASSWORD 'mypassword';"
##sudo -u postgres createdb postgres
#sudo -u postgres psql -c "ALTER USER myuser WITH SUPERUSER"





