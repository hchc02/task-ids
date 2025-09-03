## Getting Started

### Prerequisites

- Java 17
- Maven 3.6 or higher
- PostgreSQL 17 or higher

### Running the Application

1. Clone the repository:
```bash
git clone <repository-url>
```
2. Create new DB on postgreSQL 
```bash
CREATE DATABASE coding_test;
```
3. Setup username and password on application.properties based on your local setup
```bash
spring.datasource.username=postgres
spring.datasource.password=postgres
```
4. Run with Maven:
```bash
mvn clean install
then
mvn spring-boot:run
```

### SQL Script to insert data to table
src/main/resources/data.sql

The application will start on http://localhost:8080

below is curl to test api from postman
```bash
curl --location 'http://localhost:8080/api/transactions' \
--header 'Content-Type: application/json' \
--header 'X-API-Key: ••••••'
```