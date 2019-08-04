# IMDB APP

### TO RUN PROJECT 

`sbt "project web" "run"`

### TO RUN TESTS 

`sbt test`


## PROJECT BREAK DOWN 

This project is split into 3 subprojects to simulate a microservice architecture. 

**1. core**

>> This contains configurations (Logging Adapter, Config Objects, databases( Postgresql, Neo4j), and Utilities(Some utility fucntions) 
>> that will be shared across the whole project.

**2. web**

>> This is solely used to create an Http server, that runs on the port supplied in the application.conf file and it simply receives
>> the Http requests and forwards it to the service for processing, It does not contain any logic as to how the data is processed,
>> It's only logic is to present the data or response gotten from the service to the user.

**3. service**

>>This is where the logic of the application is handled. It consists of QueryResolverActors.
>> Whenever a query endpoint is hit, a unique actor is responsible for handling that query and providing the result back to the web layer.
>> The logic for each actor is separated into a trait to enable easy testing.

## Project Flow 
When the project is started using the command supplied in the second paragraph, the server listens for incoming Http Requests and then
forwards them to the relavant query resolver actor. This actor depending on the type of request, queries the right database, applies 
some logic on the result and sends the result to the sender of the message (web). Upon getting a response from the actor, the web project which is responsible for presenting 
the information to the user has some logic as to how the data will be presented to the user(sender of the http request) via case classes.

## Design considerations.

**[Postgres](https://www.postgresql.org/)** : Database used for storing movie details as well as cast and crew.

**[Neo4j](https://neo4j.com/)** : A Graph Database that was used to find the Degree of separation, could have been used to handle the coincidence query, but 
plain old Sql could also do the job.
