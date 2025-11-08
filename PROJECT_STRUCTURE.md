# IntelliDesk - Project Structure

## Complete Directory Structure

```
IntelliDesk/
│
├── README.md                           # Main project documentation
├── SETUP.md                            # Detailed setup instructions
├── LICENSE                             # MIT License
├── .gitignore                          # Git ignore rules
├── .env.example                        # Environment variables template
├── docker-compose.yml                  # Development Docker Compose
├── docker-compose.prod.yml             # Production Docker Compose
├── init-db.sql                         # Database initialization
├── build-all.sh                        # Build script for all services
│
├── config-server/                      # Centralized Configuration Service
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/intellidesk/config/
│       │   │   └── ConfigServerApplication.java
│       │   └── resources/
│       │       ├── application.yml
│       │       └── config/
│       │           ├── api-gateway.yml
│       │           ├── user-service.yml
│       │           ├── notification-service.yml
│       │           ├── rag-service.yml
│       │           └── tool-service.yml
│
├── eureka-server/                      # Service Discovery
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/intellidesk/eureka/
│       │   │   └── EurekaServerApplication.java
│       │   └── resources/
│       │       └── application.yml
│
├── api-gateway/                        # API Gateway with JWT & Rate Limiting
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/intellidesk/gateway/
│       │   │   ├── ApiGatewayApplication.java
│       │   │   ├── config/
│       │   │   │   └── GatewayConfig.java
│       │   │   ├── filter/
│       │   │   │   └── JwtAuthenticationFilter.java
│       │   │   └── util/
│       │   │       └── JwtUtil.java
│       │   └── resources/
│       │       └── application.yml
│
├── user-service/                       # User Authentication & Management
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/intellidesk/user/
│       │   │   ├── UserServiceApplication.java
│       │   │   ├── entity/
│       │   │   │   ├── User.java
│       │   │   │   ├── Role.java
│       │   │   │   ├── Permission.java
│       │   │   │   ├── Tenant.java
│       │   │   │   └── AuthProvider.java
│       │   │   ├── repository/
│       │   │   │   ├── UserRepository.java
│       │   │   │   ├── RoleRepository.java
│       │   │   │   └── TenantRepository.java
│       │   │   ├── dto/
│       │   │   │   ├── LoginRequest.java
│       │   │   │   ├── RegisterRequest.java
│       │   │   │   ├── AuthResponse.java
│       │   │   │   └── UserDto.java
│       │   │   ├── service/
│       │   │   │   ├── AuthService.java
│       │   │   │   └── UserDetailsServiceImpl.java
│       │   │   ├── controller/
│       │   │   │   └── AuthController.java
│       │   │   ├── security/
│       │   │   │   └── JwtTokenProvider.java
│       │   │   └── config/
│       │   │       └── SecurityConfig.java
│       │   └── resources/
│       │       └── application.yml
│
├── notification-service/               # Kafka-based Notification Service
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/intellidesk/notification/
│       │   │   ├── NotificationServiceApplication.java
│       │   │   ├── entity/
│       │   │   │   ├── Notification.java
│       │   │   │   ├── NotificationType.java
│       │   │   │   └── NotificationStatus.java
│       │   │   ├── repository/
│       │   │   │   └── NotificationRepository.java
│       │   │   ├── dto/
│       │   │   │   └── NotificationEvent.java
│       │   │   ├── service/
│       │   │   │   └── EmailService.java
│       │   │   └── consumer/
│       │   │       └── NotificationConsumer.java
│       │   └── resources/
│       │       ├── application.yml
│       │       └── templates/
│       │           └── welcome-email.html
│
├── rag-service/                        # AI RAG with LangChain4j & Weaviate
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/intellidesk/rag/
│       │   │   ├── RagServiceApplication.java
│       │   │   ├── config/
│       │   │   │   └── LangChainConfig.java
│       │   │   ├── dto/
│       │   │   │   ├── DocumentUploadResponse.java
│       │   │   │   ├── QuestionRequest.java
│       │   │   │   └── QuestionResponse.java
│       │   │   ├── service/
│       │   │   │   ├── DocumentService.java
│       │   │   │   └── RagService.java
│       │   │   └── controller/
│       │   │       └── RagController.java
│       │   └── resources/
│       │       └── application.yml
│
├── tool-service/                       # AI Tool Calling Agents
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/intellidesk/tool/
│       │   │   ├── ToolServiceApplication.java
│       │   │   ├── dto/
│       │   │   │   ├── SqlToolRequest.java
│       │   │   │   ├── SqlToolResponse.java
│       │   │   │   ├── NotificationToolRequest.java
│       │   │   │   ├── ApiToolRequest.java
│       │   │   │   └── ApiToolResponse.java
│       │   │   ├── service/
│       │   │   │   ├── SqlToolService.java
│       │   │   │   ├── NotificationToolService.java
│       │   │   │   └── ApiToolService.java
│       │   │   ├── controller/
│       │   │   │   └── ToolController.java
│       │   │   └── config/
│       │   │       └── WebClientConfig.java
│       │   └── resources/
│       │       └── application.yml
│
├── ui-frontend/                        # Angular 18 Frontend
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   ├── angular.json
│   ├── tsconfig.json
│   ├── tsconfig.app.json
│   └── src/
│       ├── index.html
│       ├── main.ts
│       ├── styles.scss
│       ├── environments/
│       │   ├── environment.ts
│       │   └── environment.prod.ts
│       └── app/
│           ├── app.component.ts
│           ├── app.config.ts
│           ├── app.routes.ts
│           ├── core/
│           │   ├── models/
│           │   │   ├── auth.model.ts
│           │   │   └── rag.model.ts
│           │   ├── services/
│           │   │   ├── auth.service.ts
│           │   │   └── rag.service.ts
│           │   ├── interceptors/
│           │   │   └── auth.interceptor.ts
│           │   └── guards/
│           │       └── auth.guard.ts
│           └── features/
│               ├── auth/
│               │   ├── auth.routes.ts
│               │   ├── login/
│               │   │   └── login.component.ts
│               │   └── register/
│               │       └── register.component.ts
│               ├── dashboard/
│               │   ├── dashboard.routes.ts
│               │   └── dashboard.component.ts
│               └── admin/
│                   ├── admin.routes.ts
│                   └── admin.component.ts
│
└── common-lib/                         # Shared Library
    ├── README.md
    ├── pom.xml
    └── src/
        └── main/
            └── java/com/intellidesk/common/
                ├── dto/
                │   ├── ApiResponse.java
                │   └── PageResponse.java
                ├── exception/
                │   ├── ResourceNotFoundException.java
                │   ├── BadRequestException.java
                │   └── UnauthorizedException.java
                ├── util/
                │   ├── DateTimeUtil.java
                │   └── ValidationUtil.java
                └── constants/
                    └── AppConstants.java
```

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.2.0, Spring Cloud 2023.0.0
- **Language**: Java 17
- **Build Tool**: Maven 3.8+
- **Databases**: 
  - PostgreSQL 15 (relational data)
  - Weaviate (vector database)
- **Message Queue**: Apache Kafka 7.5.0
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **AI/ML**: 
  - LangChain4j 0.27.0
  - OpenAI API (embeddings & chat)
- **Security**: Spring Security, JWT (JJWT 0.12.3), OAuth2
- **Email**: Spring Mail, Thymeleaf templates

### Frontend
- **Framework**: Angular 18
- **UI Library**: Angular Material
- **State Management**: RxJS
- **HTTP Client**: Angular HttpClient
- **Build Tool**: Angular CLI
- **Styling**: SCSS

### DevOps
- **Containerization**: Docker, Docker Compose
- **Web Server**: Nginx (for Angular)
- **Monitoring**: Spring Boot Actuator

## Key Features by Service

### 1. Config Server (Port 8888)
- Centralized configuration management
- Native file system backend
- Configuration for all microservices

### 2. Eureka Server (Port 8761)
- Service registration and discovery
- Health monitoring
- Load balancing support

### 3. API Gateway (Port 8080)
- Single entry point for all services
- JWT authentication
- Request routing
- Rate limiting (Redis-based)
- CORS configuration

### 4. User Service (Port 8081)
- User registration and login
- JWT token generation
- OAuth2 integration (Google)
- Role-based access control (RBAC)
- Multi-tenant support
- PostgreSQL persistence

### 5. Notification Service (Port 8082)
- Kafka consumer for events
- Email notifications
- Thymeleaf email templates
- Notification history logging
- PostgreSQL persistence

### 6. RAG Service (Port 8083)
- Document upload (PDF, DOC, DOCX)
- Text extraction and chunking
- OpenAI embedding generation
- Weaviate vector storage
- Semantic search
- Question answering with context

### 7. Tool Service (Port 8084)
- **SQL Tool**: Execute SELECT queries on PostgreSQL/MySQL
- **Notification Tool**: Trigger notifications via Kafka
- **API Tool**: Call external REST APIs
- WebClient for HTTP requests

### 8. UI Frontend (Port 4200/80)
- User authentication (login/register)
- Document upload interface
- Chat-based Q&A interface
- Admin panel
- Responsive design
- Material Design components

### 9. Common Library
- Shared DTOs and models
- Common exceptions
- Utility classes
- Application constants

## Service Dependencies

```
api-gateway
  ├── eureka-server
  └── config-server

user-service
  ├── eureka-server
  ├── config-server
  └── postgres

notification-service
  ├── eureka-server
  ├── config-server
  ├── postgres
  └── kafka

rag-service
  ├── eureka-server
  ├── config-server
  └── weaviate

tool-service
  ├── eureka-server
  ├── config-server
  └── kafka

ui-frontend
  └── api-gateway
```

## Getting Started

1. **Clone the repository**
2. **Configure environment variables** (see `.env.example`)
3. **Run with Docker**: `docker-compose up -d`
4. **Access the application**: http://localhost:4200

For detailed instructions, see [SETUP.md](SETUP.md)

## Contributors

Built with ❤️ using Spring Boot, Angular, and AI technologies.
