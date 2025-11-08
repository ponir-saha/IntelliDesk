# IntelliDesk - Setup Guide

## Prerequisites

Before running IntelliDesk, ensure you have the following installed:

- **Java 17+** - [Download](https://adoptium.net/)
- **Maven 3.8+** - [Download](https://maven.apache.org/)
- **Node.js 18+** and npm - [Download](https://nodejs.org/)
- **Docker** and **Docker Compose** - [Download](https://www.docker.com/)
- **PostgreSQL 14+** (if running locally without Docker)
- **OpenAI API Key** - [Get one here](https://platform.openai.com/)

## Quick Start with Docker

### 1. Clone the Repository

```bash
git clone <repository-url>
cd IntelliDesk
```

### 2. Configure Environment Variables

Copy the example environment file and update with your credentials:

```bash
cp .env.example .env
```

Edit `.env` and update:
- `POSTGRES_PASSWORD`: Strong database password
- `JWT_SECRET`: Long random string for JWT signing
- `OPENAI_API_KEY`: Your OpenAI API key
- `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`: For OAuth2 login
- Email settings for notifications

### 3. Start All Services

```bash
docker-compose up -d
```

This will start:
- PostgreSQL database
- Weaviate vector database
- Apache Kafka and Zookeeper
- All microservices
- Angular frontend

### 4. Access the Application

- **Frontend**: http://localhost:4200
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Config Server**: http://localhost:8888

## Local Development Setup

### Backend Services

1. **Build common-lib first**:
   ```bash
   cd common-lib
   mvn clean install
   cd ..
   ```

2. **Start infrastructure services** (PostgreSQL, Kafka, Weaviate):
   ```bash
   docker-compose up -d postgres kafka zookeeper weaviate
   ```

3. **Start each service**:
   ```bash
   # Config Server (start first)
   cd config-server
   ./mvnw spring-boot:run
   
   # Eureka Server (start second)
   cd ../eureka-server
   ./mvnw spring-boot:run
   
   # API Gateway
   cd ../api-gateway
   ./mvnw spring-boot:run
   
   # User Service
   cd ../user-service
   ./mvnw spring-boot:run
   
   # And so on for other services...
   ```

### Frontend Development

```bash
cd ui-frontend
npm install
npm start
```

The Angular app will run on http://localhost:4200

## Building for Production

### Build All Services

```bash
chmod +x build-all.sh
./build-all.sh
```

### Deploy with Docker

```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Configuration

### Service Ports

| Service | Port |
|---------|------|
| ui-frontend | 4200 (dev), 80 (prod) |
| api-gateway | 8080 |
| config-server | 8888 |
| eureka-server | 8761 |
| user-service | 8081 |
| notification-service | 8082 |
| rag-service | 8083 |
| tool-service | 8084 |
| PostgreSQL | 5432 |
| Kafka | 9092 |
| Weaviate | 8090 |

### Database Setup

The database schema is automatically created by JPA on first run. To manually initialize:

```bash
psql -U intellidesk -d intellidesk -f init-db.sql
```

## Features Overview

### 1. User Management
- Register new users
- Login with username/password
- OAuth2 login with Google
- Role-based access control (RBAC)
- Multi-tenant support

### 2. Document Upload & RAG
- Upload PDF, DOC, DOCX files
- Automatic embedding generation with OpenAI
- Vector storage in Weaviate
- Intelligent question answering

### 3. Notification System
- Email notifications via Kafka
- Customizable templates with Thymeleaf
- Notification history logging

### 4. AI Tools
- **SQL Tool**: Query databases (PostgreSQL/MySQL)
- **Notification Tool**: Trigger notifications
- **API Tool**: Call external REST APIs

## Troubleshooting

### Services won't start

1. Check if ports are already in use:
   ```bash
   lsof -i :8080
   ```

2. Check Docker logs:
   ```bash
   docker-compose logs -f <service-name>
   ```

### Database connection issues

1. Verify PostgreSQL is running:
   ```bash
   docker ps | grep postgres
   ```

2. Check connection settings in `config-server/src/main/resources/config/`

### OpenAI API errors

- Ensure your API key is valid and has credits
- Check rate limits on your OpenAI account

### Kafka connection issues

- Ensure Kafka and Zookeeper are running
- Check Kafka logs: `docker-compose logs kafka`

## Testing

### Backend Tests

```bash
cd <service-name>
mvn test
```

### Frontend Tests

```bash
cd ui-frontend
npm test
```

## API Documentation

Once the services are running, access Swagger UI at:
- http://localhost:8080/swagger-ui.html

## Monitoring

- **Eureka Dashboard**: http://localhost:8761
- **Actuator Health**: http://localhost:8080/actuator/health

## Support

For issues and questions:
- Create an issue in the repository
- Check the documentation in `/docs`

## License

MIT License - see LICENSE file for details
