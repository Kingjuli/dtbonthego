# DTB On The Go Banking Platform

![Banking Platform](https://img.shields.io/badge/Banking-Platform-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-lightgrey)

## Project Overview

DTB On The Go is a modern, secure banking platform designed to provide essential financial services through a microservices architecture. The platform enables users to manage their profiles and bank accounts through a robust and scalable digital banking experience.

**Key Features:**
- Secure user authentication and authorization using JWT
- Comprehensive profile management
- Multiple account types management
- Balance inquiries and transactions
- Role-based access control

**Target Users:**
- Financial institutions looking to implement digital banking solutions
- Banking customers requiring secure access to their accounts
- Developers interested in a reference implementation of a banking platform

## Architecture & Design

### Microservices Architecture

DTB On The Go implements a microservices architecture, allowing independent deployment, scaling, and maintenance of each service component:

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│                 │     │                 │     │                 │
│  Profile        │     │  Store of Value │     │  Future         │
│  Service        │◄────►  Service        │◄────►  Services       │
│                 │     │                 │     │                 │
└─────────────────┘     └─────────────────┘     └─────────────────┘
       ▲                        ▲                        ▲
       │                        │                        │
       │                        │                        │
       │                        │                        │
       ▼                        ▼                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│                       API Gateway Layer                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
                               ▲
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│                       Client Applications                       │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Design Principles

1. **Service Independence**: Each service is independently deployable with its own database
2. **RESTful Communication**: Services communicate via RESTful APIs
3. **Security First**: Comprehensive security with JWT authentication and RBAC
4. **Database per Service**: Each microservice manages its own data store
5. **API Documentation**: All endpoints are documented using OpenAPI/Swagger
6. **Common Library**: Shared code and functionality through a common library

### Component Interaction

- **Profile Service**: Manages user authentication, authorization, and profile information
- **Store of Value Service**: Handles account creation, management, and balance operations
- **Payment Service**: Processes payment transactions and integrates with external payment systems
- **Events Service**: Handles notifications and events for customer transactions
- **Common Library**: Provides shared functionality across all services

## Security Architecture

The DTB On The Go platform implements a robust security architecture using Spring Security and JWT (JSON Web Tokens) for authentication and authorization.

### Common Security Framework

All microservices share a common security framework implemented in the `common-library` module, which provides:

```
┌─────────────────────────────────────────────────────────────┐
│                   Common Security Library                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────┐    ┌─────────────────┐                 │
│  │                 │    │                 │                 │
│  │  JWT            │    │  Security       │                 │
│  │  Authentication │    │  Configuration  │                 │
│  │                 │    │                 │                 │
│  └─────────────────┘    └─────────────────┘                 │
│                                                             │
│  ┌─────────────────┐    ┌─────────────────┐                 │
│  │                 │    │                 │                 │
│  │  Exception      │    │  Utility        │                 │
│  │  Handling       │    │  Classes        │                 │
│  │                 │    │                 │                 │
│  └─────────────────┘    └─────────────────┘                 │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Key Security Components

1. **WebSecurityConfigBase**: A centralized security configuration class that:
   - Configures JWT authentication for all services
   - Defines common security rules and permitted endpoints
   - Handles CORS configuration
   - Supports service-specific authentication providers

2. **JWT Authentication**:
   - Token-based authentication using JSON Web Tokens
   - Stateless authentication for better scalability
   - Token validation and parsing through common components

3. **Authorization**:
   - Role-based access control (RBAC)
   - Method-level security with `@PreAuthorize` annotations
   - Fine-grained permission checks

4. **Exception Handling**:
   - Centralized exception handling for security-related exceptions
   - Consistent error responses across all services

### Authentication Flow

1. User authenticates with the Profile Service, providing credentials
2. Profile Service validates credentials and generates a JWT token
3. JWT token contains user ID, roles, and other claims
4. Client includes JWT token in Authorization header for subsequent requests
5. Each microservice validates the token using the common security framework
6. If valid, the request proceeds; if invalid, a 401 Unauthorized response is returned

### Security Implementation

The security implementation follows these principles:

1. **DRY (Don't Repeat Yourself)**: Common security code is defined once in the common library
2. **Defense in Depth**: Multiple layers of security controls
3. **Least Privilege**: Users only have access to what they need
4. **Secure by Default**: All endpoints require authentication unless explicitly permitted

## Tech Stack

### Backend
- **Language**: Java 17
- **Framework**: Spring Boot 3
- **Security**: Spring Security with JWT Authentication
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA
- **Build Tool**: Maven
- **Documentation**: Swagger/OpenAPI 3

### Development Tools
- **Code Quality**: SonarQube (planned)
- **Testing**: JUnit 5, Mockito
- **Code Generation**: Lombok
- **Version Control**: Git

### Infrastructure (Planned)
- **Containerization**: Docker
- **Container Orchestration**: Kubernetes
- **Service Discovery**: Eureka
- **API Gateway**: Spring Cloud Gateway
- **Configuration Management**: Spring Cloud Config

## Installation & Setup Instructions

### Prerequisites
- JDK 17 or higher
- Maven 3.6+
- PostgreSQL 14+
- Git

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/dtbonthego.git
   cd dtbonthego
   ```

2. **Configure PostgreSQL**
   ```bash
   # Create databases for each service
   createdb profile_service_db
   createdb store_of_value_db
   
   # Default credentials (customize in application.yml):
   # Username: postgres
   # Password: postgres
   ```

3. **Build all services**
   ```bash
   mvn clean install
   ```

4. **Run the Profile Service**
   ```bash
   cd profile-service
   mvn spring-boot:run
   ```

5. **Run the Store of Value Service**
   ```bash
   cd ../store-of-value-service
   mvn spring-boot:run
   ```

### Configuration

Each service has its own `application.yml` file for configuration:

- **Profile Service**: `profile-service/src/main/resources/application.yml`
- **Store of Value Service**: `store-of-value-service/src/main/resources/application.yml`

Key configuration parameters include:

- Database connection details
- Server port settings
- JWT secret and expiration time
- Logging levels

## Usage

### Authentication

1. **Register a new user**
   ```http
   POST /api/v1/auth/register
   Content-Type: application/json
   
   {
     "username": "user@example.com",
     "password": "securePassword123",
     "firstName": "John",
     "lastName": "Doe"
   }
   ```

2. **Login to get JWT token**
   ```http
   POST /api/v1/auth/login
   Content-Type: application/json
   
   {
     "username": "user@example.com",
     "password": "securePassword123"
   }
   ```

3. **Use the JWT token for authenticated requests**
   ```http
   GET /api/v1/profiles/me
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

### Account Management

1. **Create an account**
   ```http
   POST /api/v1/accounts
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   Content-Type: application/json
   
   {
     "name": "Primary Savings",
     "type": "SAVINGS",
     "profileId": "123e4567-e89b-12d3-a456-426614174000"
   }
   ```

2. **Get account details**
   ```http
   GET /api/v1/accounts/{accountId}
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

3. **Update account status**
   ```http
   PATCH /api/v1/accounts/{accountId}/status
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   Content-Type: application/json
   
   {
     "status": "ACTIVE"
   }
   ```

### API Documentation

Swagger UI is available for each service at:

- **Profile Service**: `http://localhost:8080/swagger-ui.html`
- **Store of Value Service**: `http://localhost:8081/swagger-ui.html`
