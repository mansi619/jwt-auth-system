# jwt-auth-system
Spring Boot JWT Authentication and Authorization System
# JWT Authentication System

## Project Overview

A Spring Boot backend application developed as part of a Backend Developer Machine Test.

The application provides secure user authentication, JWT-based authorization, role-based access control, product management, and country-based access restrictions.

## Technologies Used

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- Maven
- REST APIs
- Swagger / OpenAPI
- BCrypt

## Features

1. User Registration and Login
2. JWT Token Generation and Validation
3. Password Encryption using BCrypt
4. Role-Based Authorization
5. User Logout and Token Blacklisting
6. Product CRUD Operations
7. Country-Based Access Restriction
8. Global Exception Handling
9. API Documentation using Swagger

## Project Structure

```text
src/
 └── main/
     ├── java/com/onedao/auth/
     │   ├── config/
     │   ├── controller/
     │   ├── dto/
     │   ├── entity/
     │   ├── enums/
     │   ├── exception/
     │   ├── geolocation/
     │   ├── product/
     │   ├── service/
     │   └── store/
     └── resources/
         └── application.properties

pom.xml
.gitignore
README.md
```

## Database Configuration

PostgreSQL is used as the database.

Configure the following environment variables:

- DB_URL
- DB_USERNAME
- DB_PASSWORD
- JWT_SECRET
- JWT_EXPIRATION_MS

Do not store passwords or secret keys in the source code.

## Running the Application

1. Clone the repository.
2. Import the project as a Maven project in Spring Tool Suite.
3. Configure PostgreSQL.
4. Set the required environment variables.
5. Run the Spring Boot application.

## Developer

Mansi Deshpande

Backend Developer Machine Test
