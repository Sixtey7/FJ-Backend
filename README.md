# FJ-Backend

## About
Playing with the Helidon MicroProfile framework in order to create a backend for the FinanceJournal frontend that I'll eventually rewrite for the 100th time.  

## High-Level Design
Java Backend that interfaces with a PostgreSQL database through EclipseLink. Uses the JSONB datatype to store the objects in JSON for the ease of the UI that will eventually exist.  

Eventually plan to leverage k8s to deploy several docker containers (Backend, Frontend, Database, whatever else)

## TODO 
* For now, I've totally lifted the POM from Helidon.  I eventually want to turn this into a build.gradle and move over to gradle and take out what I don't want
* Think about creating schemas for the Account/Transaction object so that the UI has access to the exact same datatype.

## Related Projects
//TODO - will add when the latest and greatest frontend is started

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
    * CREATE TABLE Accounts (id VARCHAR PRIMARY KEY, data jsonb);
* Create the Transactions table
    * CREATE TABLE Transactions (id VARCHAR PRIMARY KEY, data jsonb);
* Profit!

### Build the Service
* From the command line, run the command
    * mvn package

### Run the Service
* From the command line, run the command
    * java -jar target/fjservice.jar
### Sample REST Requests
#### Accounts
* Get All Accounts
    * curl http://localhost:8080/accounts
* Put in an Account
    * curl -XPUT -H "Content-type: application/json" -i -d '{"name":"test"}' http://localhost:8080/accounts

### Transactions
* Get All Transactions
    * curl http://localhost:8080/transactions
* Get For An Account
    * curl -i  http://localhost:8080/transactions/forAccount/e31f6a71-0aab-44ba-96b0-b7d756aec187
* Put in a Transaction
    * curl -XPUT -H "Content-type: application/json" -i -d '{"accountId": "e31f6a71-0aab-44ba-96b0-b7d756aec187", "name":"Hello", "amount": 12345}' http://localhost:8080/transactions
* Import transactions
    * curl -XPUT -H "Content-Type: text/plain" -i --data-binary @<CSV_LOCATION> http://localhost:8080/transactions/import/<Account_UUID>

##TODO
[ ] Try Out Quarkas (https://quarkus.io/guides/getting-started-guide)

[ ] Standardize to a single logging platform

## References
https://helidon.io/
