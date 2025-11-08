# IntelliDesk

An enterprise-grade microservices-based intelligent helpdesk system with AI-powered knowledge retrieval (RAG), multi-tenancy, and comprehensive user management.

## 🏗️ Architecture Overview

IntelliDesk is built using a microservices architecture with the following components:

### Core Services
- **config-server**: Centralized configuration management using Spring Cloud Config
- **eureka-server**: Service discovery and registration using Netflix Eureka
- **api-gateway**: Entry point for all services with JWT authentication and rate limiting

### Business Services
- **user-service**: User authentication, authorization, role-based access control (RBAC), OAuth2 integration
- **notification-service**: Kafka-based notification system with email templates
- **rag-service**: AI Knowledge Retrieval using LangChain4j, Weaviate vector DB, and OpenAI embeddings
- **tool-service**: AI agent tools for SQL queries, notifications, and API integrations

### Frontend
- **ui-frontend**: Angular 18 single-page application with authentication, chat interface, and admin panel

### Shared
- **common-lib**: Shared DTOs, utilities, and models across microservices

## 🚀 Features

### User Management
- JWT-based authentication
- OAuth2 social login (Google)
- Role-based access control (RBAC)
- Multi-tenant support
- User registration and profile management

### AI-Powered Knowledge Base
- Document upload (PDF, DOC, DOCX)
- Automatic embedding generation using OpenAI
- Vector-based semantic search using Weaviate
- RAG (Retrieval Augmented Generation) for intelligent Q&A

### Intelligent Tools
- **SQL Tool**: Execute database queries across PostgreSQL/MySQL
- **Notification Trigger Tool**: Send notifications via Kafka
- **API Invoker Tool**: Integrate with external REST APIs

### Notification System
- Kafka-based event-driven architecture
- Email notifications with customizable templates
- Notification history and logging

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.x, Spring Cloud
- **Languages**: Java 17+
- **Databases**: PostgreSQL, Weaviate (Vector DB)
- **Message Queue**: Apache Kafka
- **AI/ML**: LangChain4j, OpenAI API
- **Security**: Spring Security, JWT, OAuth2

### Frontend
- **Framework**: Angular 18
- **UI Components**: Angular Material / PrimeNG
- **State Management**: NgRx (optional)
- **HTTP Client**: Angular HttpClient

### DevOps
- **Containerization**: Docker, Docker Compose
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Configuration**: Spring Cloud Config

## 📋 Prerequisites

- Java 17 or higher
- Node.js 18+ and npm
- Docker and Docker Compose
- PostgreSQL 14+
- Apache Kafka
- Weaviate (vector database)
- OpenAI API key (for embeddings and RAG)

## 🏃 Quick Start

### 1. Clone the repository
```bash
git clone <repository-url>
cd IntelliDesk
```

### 2. Configure environment variables
Create a `.env` file in the root directory:
```env
# Database
POSTGRES_USER=intellidesk
POSTGRES_PASSWORD=your_password
POSTGRES_DB=intellidesk

# JWT
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRATION=86400000

# OAuth2
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# OpenAI
OPENAI_API_KEY=your_openai_api_key

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_email_password
```

### 3. Start all services with Docker Compose
```bash
docker-compose up -d
```

### 4. Access the services
- **Frontend UI**: http://localhost:4200
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Config Server**: http://localhost:8888

## 📦 Service Ports

| Service | Port |
|---------|------|
| ui-frontend | 4200 |
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

## 🔧 Development Setup

### Backend Services (Spring Boot)
```bash
cd <service-name>
./mvnw clean install
./mvnw spring-boot:run
```

### Frontend (Angular)
```bash
cd ui-frontend
npm install
npm start
```

## 📚 API Documentation

Once the services are running, you can access the API documentation:
- Swagger UI: http://localhost:8080/swagger-ui.html

## 🧪 Testing

### Run backend tests
```bash
cd <service-name>
./mvnw test
```

### Run frontend tests
```bash
cd ui-frontend
npm test
```

## 🚢 Deployment

### Build all services
```bash
./build-all.sh
```

### Deploy with Docker
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## 📖 Documentation

- [Architecture Guide](docs/architecture.md)
- [API Documentation](docs/api.md)
- [Setup Guide](docs/setup.md)
- [Developer Guide](docs/development.md)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- Your Name - Initial work

## 🙏 Acknowledgments

- Spring Boot and Spring Cloud communities
- LangChain4j developers
- Angular team
- OpenAI for embeddings API
