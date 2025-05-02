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

## Architecture & Services

The application consists of the following microservices:

1. **Profile Service (Port 8081)**: Handles user authentication, registration, and profile management
2. **Store of Value Service (Port 8082)**: Manages bank accounts and balances
3. **Payment Service (Port 8083)**: Processes transactions (deposits, withdrawals, transfers)
4. **Events Service (Port 8084)**: Handles event processing and notifications

> **Note:** A comprehensive system architecture document is available in the `design_docs` directory. This document details the technical design, security implementation, data consistency strategies, scalability approach, and disaster recovery planning for the DTB On The Go platform.

## Deployment

### Prerequisites

- Docker Engine (20.10+)
- Docker Compose (v2.0+)
- Maven (3.6+) (for building from source)
- Git (for cloning the repository)

### Deployment Instructions

To deploy the entire application, simply run the provided deployment script:

```bash
# Run the deployment script
./deploy.sh
```

This interactive script will:
1. Check prerequisites (Docker and Docker Compose)
2. Offer to create a default `.env` file with environment variables
3. Ask if you want to build Docker images
4. Ask if you want to clean up existing containers
5. Start all services using Docker Compose
6. Display access URLs and health check endpoints

### Environment Configuration

The deployment script will offer to create a `.env` file with default environment values. 

## Common Operations

### Viewing Service Status

```bash
docker-compose ps
```

### Stopping the Services

```bash
docker-compose down
```

To stop and remove volumes as well:

```bash
docker-compose down -v
```

### Viewing Container Logs

```bash
docker-compose logs -f [service-name]
```

Example: `docker-compose logs -f profile-service`

### Accessing the Database

```bash
docker exec -it dtbonthego-postgres psql -U postgres -d dtbonthego
```

## API Usage

### Authentication

```http
# Register a new user
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "securePassword123",
  "firstName": "John",
  "lastName": "Doe"
}

# Login to get JWT token
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "securePassword123"
}
```

### Account Management

```http
# Create an account (requires authentication)
POST /api/v1/accounts
Authorization: Bearer [YOUR_JWT_TOKEN]
Content-Type: application/json

{
  "name": "Primary Savings",
  "type": "SAVINGS",
  "profileId": "123e4567-e89b-12d3-a456-426614174000"
}
```

## Troubleshooting

### Service Startup Issues

If services fail to start:

1. Check if the Docker images were built correctly: `docker images dtbonthego/*`
2. Verify that all required ports are available
3. Check container logs: `docker-compose logs [service-name]`

### Database Connectivity

If services can't connect to the database:

1. Verify the PostgreSQL container is running: `docker ps | grep postgres`
2. Check PostgreSQL logs: `docker-compose logs postgres`
3. Ensure the database URL, username, and password are correct in the environment variables

### API Documentation

Swagger UI is available for each service at:

- **Profile Service**: `http://localhost:8081/swagger-ui.html`
- **Store of Value Service**: `http://localhost:8082/swagger-ui.html`
- **Payment Service**: `http://localhost:8083/swagger-ui.html`
- **Events Service**: `http://localhost:8084/swagger-ui.html` 