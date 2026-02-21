Java 17+
🔹 What it is

A programming language used to write backend applications.

🔹 What it does

Executes your business logic, APIs, authentication, validation, etc.

🔹 How it's used here

Write controllers

Write services

Write entities

Write business rules

🔹 Why Java 17?

Long-term support (LTS)

Stable

Used widely in industry

🔹 Alternatives

Kotlin

Node.js (JavaScript)

Python (Django/FastAPI)

Go

2️⃣ Spring Boot
🔹 What it is

A framework built on Spring to quickly create production-ready backend apps.

🔹 What it does

Auto configuration

Embedded server

Dependency injection

REST API support

Security integration

🔹 How it's used in your project

Create REST APIs

Handle authentication

Connect to database

Manage application lifecycle

🔹 Alternatives

Spring MVC (without Boot)

Micronaut

Quarkus

Express.js (Node)

3️⃣ Spring Web
🔹 What it does

Allows you to create REST endpoints using:

@RestController
@GetMapping
@PostMapping

Used to expose APIs like:

POST /api/jobs
4️⃣ Spring Data JPA
🔹 What it is

Abstraction over database access.

🔹 What it does

Maps Java objects to database tables

Reduces SQL writing

Handles CRUD automatically

🔹 Used in your project

For:

UserRepository

JobRepository

CompanyRepository

🔹 Alternatives

JDBC

MyBatis

Hibernate directly

5️⃣ Hibernate
🔹 What it is

ORM (Object Relational Mapping) engine behind JPA.

🔹 What it does

Converts Java objects → SQL queries

Handles relationships

Manages transactions

🔹 Used for

Entity mapping:

@Entity
@Table(name = "users")
6️⃣ PostgreSQL
🔹 What it is

Relational database.

🔹 What it does

Stores:

Users

Companies

Jobs

Applications

🔹 Why use it?

Production-ready

Advanced indexing

Widely used in industry

🔹 Alternatives

MySQL

MongoDB (NoSQL)

7️⃣ Spring Security
🔹 What it does

Secures endpoints

Handles login

Manages authentication flow

🔹 In your project

Protect /api/jobs

Role-based access

JWT validation

8️⃣ JWT (JSON Web Token)
🔹 What it is

A token-based authentication system.

🔹 What it does

User logs in

Backend generates token

Token sent in header

Backend verifies token

🔹 Why important?

Stateless authentication — scalable.

9️⃣ Lombok
🔹 What it does

Removes boilerplate code like:

getters

setters

constructors

Example:

@Getter
@Setter
🔟 Swagger (OpenAPI)
🔹 What it does

Auto-generates API documentation.

Used for:

/swagger-ui.html
1️⃣1️⃣ Docker
🔹 What it is

Containerization tool.

🔹 What it does

Packages your backend + DB into containers.

🔹 Why needed?

Deployment

Environment consistency

DevOps skill

1️⃣2️⃣ Git + GitHub
🔹 What it does

Version control

Collaboration

Code reviews

Branching