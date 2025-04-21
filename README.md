# Introduction
The project was a test task.

**Simplified requirements:**
1. Implement a Java (Spring Boot) application which serves three endpoints to create, update(with limited time), and search orders.
2. The search endpoint should be only for authenticated customers, and everything else is public.
3. API documentation.
4. Test coverage 80%.
5. Dockerize the application.
6. Meaningful documentation about the project.

## Technical Implementation
- Postgres as the primary database
- Flyway migrations
- Spring Boot 3.4.4
- JPA(Hibernate)
- Java 21
- Docker and Docker Compose
- Swagger API documentation
- Optimistic locks
- Session-based authentication(in-memory)
- Pagination

## Getting Started

### Prerequisites
- Unix-like operating system (for Windows manually execute commands from shell scripts and use `mvnw.cmd` instead)
- Docker and Docker Compose

### Installation

1. **Build the Project**
```bash
./build.sh
```

2. **Start the Application**
```bash
docker compose up -d
```

The application is available at: http://localhost:8080  
API documentation: http://localhost:8080/swagger-ui/index.html  
Database credentials:
```
URL: jdbc:postgresql://localhost:5432/orders
Username: user
Password: password
```

3. **Stop the Application**
```bash
docker compose down
```

4. **Clean Up**
```bash
./clean.sh  # Removes local docker image and temp file with sessions(created by curl)
```

## Usage Examples

### Place an order(not secure endpoint)
```bash
curl --location --request POST 'http://localhost:8080/orders' \
--header 'Content-Type: application/json' \
--data-raw '{
    "firstName": "Mike",
    "lastName": "Johnson",
    "phone": "5552223333",
    "deliveryAddress": "5th Avenue, 13",
    "pilotes": 15
}'| jq .
```

### Update the order (changes the number of pilotes)
```bash
curl --location --request PUT 'http://localhost:8080/orders' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": 1,
    "firstName": "Mike",
    "lastName": "Johnson",
    "phone": "5552223333",
    "deliveryAddress": "5th Avenue, 13",
    "pilotes": 10
}'
```

To use the search endpoint, which is secured, we have to log in first.  
**NOTE:** here you can get credentials of registered users [flyway-migration](https://github.com/ggruzdov/sqrd-demo/blob/main/src/main/resources/db/migration/V1.02__add_customers.sql)

### Login
```bash
curl -v -c --location --request POST 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "phone": "5552223333",
    "password": "mike1234"
}'
```

### Search orders
**NOTE:** in this demo we use session-based authentication, so the JSESSIONID cookie should be passed in the request. 
Here the cookie will be added automatically if you have followed the scripts one by one.
```bash
curl -b --location --request GET 'http://localhost:8080/orders/search?firstName=ke' | jq .
```

## Future Improvements
1. Proper indexing and case-insensitive queries for the 'like' operator.
2. Detailed description of each DTO for API documentation.
3. Observability.
