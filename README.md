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
4. Tests, assignment-script compatibility, and final polish - complete

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

On this machine, IntelliJ's bundled Maven can be used with the project-local Maven settings:

```powershell
& 'C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.3\plugins\maven\lib\maven3\bin\mvn.cmd' -s maven-settings.xml test
```

## Render Deployment With Docker

Create a Render Web Service with:

```text
Runtime: Docker
Dockerfile Path: ./Dockerfile
```

Render does not need a separate build command or start command when using this Dockerfile.
