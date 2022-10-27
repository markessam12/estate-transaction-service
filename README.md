# Estate Transactions Application
This project is a small simple web service application.
It provides a system in which users (owners) can delcare their ownership of Estates (Properties) and can trade and sell their estates.
Each property bought or sold will be stored as a transaction inside the database.

## Technologies used
1. Jax-rs for REST API's
2. Jax-ws for SOAP web service
3. Glassfish server
4. Aerospike database
5. Docker
6. Junit and mockito for unit testing
7. MapStruct
8. postman

## Instalation prerequistes
1. Java 8
2. Glassfish V6.0
3. Jakarta EE
4. Docker with aerospike CE

## Diagrams
### UML Class Diagram
![Class Diagram](https://user-images.githubusercontent.com/63367712/198333713-e5421a1e-ff08-4100-8b35-c0ec99392447.PNG)

### Data flow Diagram
![Data flow diagram](https://user-images.githubusercontent.com/63367712/198333861-82771cda-047e-4aa5-a7a3-61032b6deb5d.PNG)

## REST requests examples
### Get owners in database
![image](https://user-images.githubusercontent.com/63367712/198364271-b1e47d09-591a-4e26-bd29-b2623f1f9dbb.png)

### Get owner of specific username
![image](https://user-images.githubusercontent.com/63367712/198364441-7e7cdeaa-74b1-4c6b-abc8-3598260856ee.png)

### Get property of specific id
![image](https://user-images.githubusercontent.com/63367712/198364626-32c35356-3dc5-4578-bd05-80f3a33ee358.png)

### Put first and last name of an owner
![image](https://user-images.githubusercontent.com/63367712/198365151-7f05798a-e06e-4577-855f-6089b551c2fe.png)

### Get transactions in the database
![image](https://user-images.githubusercontent.com/63367712/198363987-376bad84-b8b9-4519-a90d-15004a44df1d.png)

## In this project, the next topics were practices:
1. Usage of the building tool gradle
2. Exposing different rest apis and usage of different response codes
3. Application of HATEOAS constraints
3. Usage of the three-tier architecture (Data Access layer, Business Logic layer, and Presentation layer)
4. Usage of singleton and builder design patterns
5. Usage of DTO and DAO pattern
6. Difference between checked and unchecked exceptions
7. Exception handling and chaining
8. Unit testing good practices
