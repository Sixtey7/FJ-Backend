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

## References
https://helidon.io/