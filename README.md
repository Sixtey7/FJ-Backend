# FJ-Backend

## About
Playing with the Helidon MicroProfile framework in order to create a backend for the FinanceJournal frontend that I'll eventually rewrite for the 100th time.  

## High-Level Design
Java Backend that interfaces with a PostgreSQL database through EclipseLink. Uses the JSONB datatype to store the objects in JSON for the ease of the UI that will eventually exist.  

Eventually plan to leverage k8s to deploy several docker containers (Backend, Frontend, Database, whatever else)


## Related Projects
[FJ-Frontend](https://github.com/Sixtey7/FJ-Frontend)

## Getting Started

### Create the Database
* From the command line, run the command
    * sudo -u postgres createdb fjdb
* Then create the user by running the command
    * sudo -u postgres createuser -P fjuser
* Then login as postgres to psql
    * sudo -u postgres psql postgres
* Run the command
    * GRANT ALL PRIVILEGES ON DATABASE fjdb TO fjuser;
* Exit the database and log in as fjuser
    * psql -U fjuser fjdb
* Create the Accounts table
    * CREATE TABLE Accounts (id VARCHAR PRIMARY KEY, name text, amount real, notes text, dynamic boolean);
* Create the Transactions table
    * CREATE TABLE Transactions (id VARCHAR PRIMARY KEY, account_id VARCHAR, name text, date date, amount real, type text, notes text);
* Profit!

### Build the Service
* From the command line, run the command
    * gradle build

### Build the Service for the pi
* From the command line, run the command
    * gradle build -Dquarkus.profile=pi

### Run the Service
* From the command line, run the command
    * java -jar build/fj-service-1.0.0-SNAPSHOT-runner.jar
### Sample REST Requests
#### Accounts
* Get All Accounts
    * curl http://localhost:8081/accounts
* Put in an Account
    * curl -XPUT -H "Content-type: application/json" -i -d '{"name":"test", "dynamic": "true"}' http://localhost:8081/accounts

### Transactions
* Get All Transactions
    * curl http://localhost:8081/transactions
* Get For An Account
    * curl -i  http://localhost:8081/transactions/forAccount/e31f6a71-0aab-44ba-96b0-b7d756aec187
* Get Transactions Between Two Dates
    * curl http://raspberrypi:8081/transactions/betweenDates/2020-03-21/2020-04-21
* Put in a Transaction
    * curl -XPUT -H "Content-type: application/json" -i -d '{"accountId": "e31f6a71-0aab-44ba-96b0-b7d756aec187", "name":"Hello", "amount": 12345, "date": "2020-04-20"}' http://localhost:8081/transactions
* Import transactions
    * curl -XPUT -H "Content-Type: text/plain" -i --data-binary @<CSV_LOCATION> http://localhost:8081/transactions/import/<Account_UUID>

## Docker
### Starting postgres
* docker run --rm --name pg-docker -e POSTGRES_PASSWORD=docker -d -p 5432:5432 -v /data/postgres:/var/lib/postgresql/data postgres

### Run backend with raspberrypi postgres
* docker run --name agt-backend -p 5000:5000 -e DB_LOC=/data/sqlite -e DB_TYPE=postgres -e SERVER_URL=raspberrypi -v /data/sqlite:/data/sqlite -d agt-backend:latest

### Build the Container
* docker build -t fj-backend:latest .

### Run the Container
* docker run --name fj-backend -p 8081:8081 -d fj-backend:latest
