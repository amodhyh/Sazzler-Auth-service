# Sazzler Auth Service

## Overview
Handles user authentication, registration, JWT token generation, and role-based access control (RBAC).

## Features
- User registration and login
- JWT token issuance and validation
- Role and permission management
- Integration with database (e.g., MongoDB, PostgreSQL)
- Docker support

## Setup
1. Java 21+
2. Gradle
3. Configure database connection in `application.yaml`
4. Set JWT secret in environment or config

## Build & Run
```bash
./gradlew build
./gradlew bootRun
```

## Configuration
- Database: Set URI, username, password in `application.yaml`
- JWT: Set secret and expiration
- Eureka: Set service registry URL

## API Endpoints
- `/auth/register` - Register new user
- `/auth/login` - Authenticate and get JWT

## Docker
- Build: `docker build -t sazzler-auth-service .`
- Run: `docker run -p 8081:8081 sazzler-auth-service`

## Troubleshooting
- Check DB connection
- Validate JWT secret
- Ensure Eureka is running

