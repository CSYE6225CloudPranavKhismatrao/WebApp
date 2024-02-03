# Cloud Native Web Application - User Assignment Management

The repository contains source code for a cloud-native web application that manages user assignments using Spring Boot, PostgreSQL, and Maven. 
The application is hosted on a Debian 12 VM in Digital Ocean. 


## AWS Architecture
User submits an assessment via the the POST method to /v1/assignments/{id}/submission. Here's a sample payload:

1. User submits an assessment via the the POST method to /v1/assignments/{id}/submission. Here's a sample payload:
  ```
{
"submission_url":  "https://github.com/tparikh/myrepo/archive/refs/tags/v1.0.0.zip"
}
```

2. Upon passing validations, the application leverages AWS SNS SDK to publish a message to an SNS topic. The message has the following format:
```
{
    "email": "user@example.com",
    "attempt": 1,
    "id": "ba45b5e2-5e5c-448f-8881-01ab357c4c67",
    "submissionID": "81568826-4609-437c-a4cc-5ae3fe6ab94a",
    "url": "https://github.com/tparikh/myrepo/archive/refs/tags/v1.0.0.zip",
    "status": "SUCCESS",
    "message": "",
    "timestamp": "2023-12-05T22:22:27.713015948Z"
}

```

3. A Lambda function is triggered when a message is published to a specific topic. The function then parses the JSON payload, executing the following tasks: fetching the submission ZIP from the user-specified endpoint, transferring the ZIP to a GCP Cloud Storage bucket, generating a new entry for the submission in a DynamoDB table, and utilizing SMTP to send an email to the user, providing information on the submission's status and storage details.

AWS Components:


| AWS Component/ |  Description  | 
|----------------|:-------------:| 
| Route53        | Hosted zone for the custom domain houses A, NS, SOA, TXT and CNAME type DNS records for routing traffic to load balancer and authorizating mailgun email server | 
| EC2      |   The web application runs of t2.micro instances with a custom AMI backed by debian-12-amd64 linux distribution    |
| Application Load Balancer  |   Listens for traffic on port 443 and forwards it to a target group of our web application on port 80   |
| AutoScaling Group |  Scales the number of instances in the target group based on the traffic   |
| RDS |  Managed PostgreSQL database for storing user and assignment data   |
| S3 |  Stores the user.csv file and the application jar file   |
| SNS |  Publishes a message to a topic when a user submits an assignment   |
| Lambda |  Triggers a function when a message is published to a specific topic. The function then parses the JSON payload, executing the following tasks: fetching the submission ZIP from the user-specified endpoint, transferring the ZIP to a GCP Cloud Storage bucket, generating a new entry for the submission in a DynamoDB table, and utilizing SMTP to send an email to the user, providing information on the submission's status and storage details.   |
| DynamoDB |  Stores the submission data   |
| CloudWatch |  Monitors the health of the web application and the RDS instance   |





## Prerequisites
- Java 19: Ensure Java Development Kit (JDK) 17 is installed.
- Maven: This project uses the Maven build system.
- PostgreSQL: A running instance is required for bootstrapping and integration tests.


## Setup & Installation
### Database Configuration
- Database (PostgreSQL) is configured and instance up and running. using the following commands:
  - Start Postgres:
    `sudo service postgresql start`
  - See Status:
    `sudo service postgresql status`
  - Stop Service:
    `sudo service postgresql start`
- Configured the application.properties file with the appropriate database credentials.
- The application will auto-bootstrap the database schema upon startup.

### Loading User Data
- User data will be loaded in a CSV format.
- CSV file is in the location /opt/user.csv.
- When the application starts, it will automatically pick up this file, load the users, and store their hashed passwords in the database.

### Running the Application
- mvn clean install

### Integration Testing
- Tests have been implemented for the /healtz endpoint. These tests are run against real MySQL and PostgreSQL instances in the CI environment.

### Continuous Integration
- Integrated GitHub Actions for CI/CD, which runs the integration tests for each PR.
- Branch protection rules are added. So, a PR can't be merged unless the GitHub Actions workflow gives is successful.

## Digital Ocean
-  Application will be showcased from a Debian 12 VM hosted in Digital Ocean
-  A setup.sh script is created to setup the environment (Java Maven Postgressql) for Debian VM
-  A seperate config property file is created to store the db credentials out of the web application
-  The project zip folder. users.csv, db.properties is scp to the /opt folder in debian
-  A start.sh Script is created to unzip and run the jar file in the project

## Necessary Commands used and required

- sudo -i -u postgres
- psql
- ALTER USER myuser WITH PASSWORD 'mypassword';
- CREATE DATABASE postgres;
- GRANT ALL PRIVILEGES ON DATABASE postgres TO myuser;
- \q -  to quit psql
- sudo systemctl restart postgresql

- scp "C:\PRANAV\MS\Northeastern\SEM 3 - Fall 2023\CLOUD\Pranav_Khismatrao_002746375_03.zip" root@161.35.8.17:/opt/

- sudo rm -rf /opt/Cloud/ -  to delete existing unzipped folder
- sudo rm -rf /Cloudapplication.zip

- psql -U superuser_name -d database_name
- psql -U postgres -d userdata -  to enter the db
- \l - to list databases
- \dt - to list tables
- \c  userdata - to connect to db
- \dp userdata - to see the permissions
- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO userdata;
- DROP table userdata CASCADE;
- DROP table assignmentdata CASCADE
- DROP table userdata CASCADE;
