# Personal Finance Manager API

Spring Boot REST API for the Personal Finance Manager assignment.

## Tech Stack

- Java 17+
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- H2 Database
- Maven
- JUnit 5, Mockito, JaCoCo

## Current Build Phases

1. Project foundation, configuration, and package structure - complete
2. Domain entities, repositories, DTOs, and services - complete
3. Controllers, session authentication, validation, and exception handling - complete
4. Assignment test-script alignment and final polish

## Local Run

```bash
mvn spring-boot:run
```

Base URL:

```text
http://localhost:8080/api
```

Health check:

```text
GET /api/health
```

## Tests

```bash
mvn test
```
