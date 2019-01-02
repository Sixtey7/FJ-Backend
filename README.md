# FJ-Backend

## About
Playing with the Helidon MicroProfile framework in order to create a backend for the FinanceJournal frontend that I'll eventually rewrite for the 100th time.  

## High-Level Design
Java Backend that will interface with a PostgreSQL database.  Planning to take advantage of the JSONB storage type to store mostly JSON for ease of access with the frontend

Eventually plan to leverage k8s to deploy several docker containers (Backend, Frontend, Database, whatever else)

## TODO 
* For now, I've totally lifted the POM from Helidon.  I eventually want to turn this into a build.gradle and move over to gradle and take out what I don't want
* Write some Code!

## Related Projects
//TODO - will add when the latest and greatest frontend is started

## References
https://helidon.io/