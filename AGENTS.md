# Customers Backend

## Project Context

The application is a `customers` microservice responsible for:

* Customer creation.
* Customer search by DNI.
* Customer search by email.
* Listing all customers when no filters are provided.
* Customer birth indicators grouped by month and year.

Do not introduce unnecessary architectural complexity.

## Technology Stack

* Java 17
* Spring Boot
* Maven
* Spring Web
* Spring Data JPA
* Bean Validation
* H2 Database
* JUnit 5
* Mockito

Use only dependencies that provide a clear benefit to the requirements.

Do not add libraries or frameworks without justification.

## Architecture

Packages:

```text
com.example.customers
├── controller
├── dto
├── entity
├── exception
├── repository
├── service
└── CustomersApplication.java
```

Responsibilities:

### controller

REST API layer only.

Controllers must:

* Receive HTTP requests.
* Validate request input.
* Call the service layer.
* Return appropriate HTTP responses.

Controllers must not contain business logic or database access.

### service

Contains business logic.

The service layer is responsible for:

* Customer creation.
* Validation of unique customer identifiers (DNI and email).
* Customer filtering.
* Indicator calculations.
* Entity/DTO mapping when appropriate.

Do not create interfaces with a single implementation unless there is a concrete reason to do so.

### repository

Contains database access using Spring Data JPA.

Do not put business logic in repositories.

### entity

Contains JPA entities.

Do not expose JPA entities directly from REST endpoints.

### dto

Use explicit DTOs for API requests and responses.

Prefer clear names such as:

* `CreateCustomerRequest`
* `CustomerResponse`
* `CustomerIndicatorsResponse`
* `BirthRateByMonthResponse`

Java records may be used for DTOs when appropriate.

### exception

Centralize REST error handling using `@RestControllerAdvice`.

Create custom exceptions only when they improve clarity.

## Domain Model

A customer contains:

* id
* nombre
* apellido
* email
* dni
* fechaCreacion
* fechaNacimiento

Keep the business/API field names in Spanish.

Do not rename them to:

* firstName
* lastName
* documentNumber
* createdAt
* birthDate

Use:

```text
nombre
apellido
email
dni
fechaCreacion
fechaNacimiento
```

## Data Types

Use appropriate Java types.

Recommended:

```text
id              Long
nombre          String
apellido        String
email           String
dni             String
fechaCreacion   LocalDateTime
fechaNacimiento LocalDate
```

DNI must be modeled as `String`, not a numeric type.

## Customer Creation

The API client must not provide `fechaCreacion`.

The backend must generate it automatically when the customer is created.

Customer creation must validate at least:

* nombre is required.
* apellido is required.
* email is required and has valid email format.
* dni is required.
* fechaNacimiento is required.
* fechaNacimiento cannot be in the future.

Email and DNI must be unique.

Do not silently overwrite an existing customer.

## API

Base path:

```text
/api/customers
```

Expected operations:

```text
POST /api/customers
GET  /api/customers
GET  /api/customers?dni={dni}
GET  /api/customers?email={email}
GET  /api/customers/indicadores
```

Filters on `GET /api/customers` must be optional.

Without filters, return all customers.

## Indicators

The indicators endpoint must provide:

* Number of customers born per month/year.
* Month/year with the highest number of customer births.
* Month/year with the lowest number of customer births.
* Birth rate for each month/year.

For this assessment, because no external population value is provided, define birth rate as:

```text
customers born in month/year
---------------------------- × 100
total customers
```

Indicator calculations must use `fechaNacimiento`, not `fechaCreacion`.

Handle the empty-database case safely.

## Code Style

Prefer readable, explicit code over clever abstractions.

Use:

* Constructor injection.
* Clear method names.
* Small cohesive methods.
* Proper HTTP status codes.
* Bean Validation.
* `Optional` where appropriate at repository boundaries.

Avoid:

* Field injection with `@Autowired`.
* Generic catch-all exceptions.
* Excessive inheritance.
* Hexagonal architecture.
* CQRS.
* Event sourcing.
* unnecessary design patterns.
* premature abstractions.
* unnecessary interfaces.
* unnecessary mapping libraries.

Do not add Lombok unless explicitly requested.

## Error Handling

Use consistent error responses.

At minimum handle:

* Invalid request data.
* Duplicate DNI.
* Duplicate email.
* Unexpected errors when appropriate.

Do not expose stack traces or internal implementation details in API responses.

## Database

H2 is used to simplify local execution and technical assessment review.

The persistence layer must remain based on Spring Data JPA.

Do not introduce database-specific behavior unless necessary.

## Commands

Run the application on Windows:

```bash
.\mvnw.cmd spring-boot:run
```

Run tests:

```bash
.\mvnw.cmd test
```

Build:

```bash
.\mvnw.cmd clean package
```

## Agent Working Rules

Before changing code:

1. Inspect the existing project.
2. Respect the current package structure.
3. Do not recreate files that already exist.
4. Do not change dependencies unless required.
5. Do not implement functionality outside the requested task.

For every task:

1. Explain briefly what files will be changed.
2. Implement only the requested scope.
3. Run relevant tests or compilation.
4. Report what was changed.
5. Report any assumptions.
6. Do not create a Git commit unless explicitly requested.

Never implement future steps preemptively.
