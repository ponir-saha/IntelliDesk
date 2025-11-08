# IntelliDesk - Quick Reference

## 🚀 Quick Start Commands

### Start Everything with Docker
```bash
# Copy and configure environment
cp .env.example .env
# Edit .env with your credentials

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Local Development

```bash
# 1. Build common library
cd common-lib && mvn clean install && cd ..

# 2. Start infrastructure
docker-compose up -d postgres kafka zookeeper weaviate

# 3. Start services in order
cd config-server && mvn spring-boot:run &
cd eureka-server && mvn spring-boot:run &
cd api-gateway && mvn spring-boot:run &
cd user-service && mvn spring-boot:run &
cd notification-service && mvn spring-boot:run &
cd rag-service && mvn spring-boot:run &
cd tool-service && mvn spring-boot:run &

# 4. Start frontend
cd ui-frontend && npm install && npm start
```

## 📍 Access Points

| Service | URL | Description |
|---------|-----|-------------|
| Frontend | http://localhost:4200 | Angular UI |
| API Gateway | http://localhost:8080 | Main API endpoint |
| Eureka | http://localhost:8761 | Service registry |
| Config Server | http://localhost:8888 | Configuration |
| User Service | http://localhost:8081 | Auth endpoints |
| Notification | http://localhost:8082 | Notifications |
| RAG Service | http://localhost:8083 | AI/RAG |
| Tool Service | http://localhost:8084 | AI Tools |

## 🔑 API Endpoints

### Authentication (via API Gateway)
```bash
# Register
POST http://localhost:8080/api/auth/register
Content-Type: application/json
{
  "email": "user@example.com",
  "username": "testuser",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}

# Login
POST http://localhost:8080/api/auth/login
Content-Type: application/json
{
  "username": "testuser",
  "password": "password123"
}

# Response includes token
{
  "token": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "type": "Bearer",
  "user": {...}
}
```

### RAG Service
```bash
# Upload document
POST http://localhost:8080/api/rag/documents/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data
file: <PDF/DOC/DOCX file>

# Ask question
POST http://localhost:8080/api/rag/question
Authorization: Bearer <token>
Content-Type: application/json
{
  "question": "What is the main topic of the uploaded documents?",
  "maxResults": 5
}
```

### Tool Service
```bash
# SQL Query
POST http://localhost:8080/api/tools/sql
Authorization: Bearer <token>
Content-Type: application/json
{
  "datasourceType": "postgresql",
  "host": "localhost",
  "port": "5432",
  "database": "intellidesk",
  "username": "intellidesk",
  "password": "password",
  "query": "SELECT * FROM users LIMIT 10"
}

# Send Notification
POST http://localhost:8080/api/tools/notification
Authorization: Bearer <token>
Content-Type: application/json
{
  "recipient": "user@example.com",
  "subject": "Test Notification",
  "template": "welcome-email",
  "variables": {
    "name": "John Doe"
  },
  "type": "EMAIL"
}

# API Call
POST http://localhost:8080/api/tools/api
Authorization: Bearer <token>
Content-Type: application/json
{
  "url": "https://api.example.com/data",
  "method": "GET",
  "headers": {
    "Accept": "application/json"
  }
}
```

## 🐳 Docker Commands

```bash
# Build all images
docker-compose build

# Start specific service
docker-compose up -d user-service

# View service logs
docker-compose logs -f user-service

# Restart service
docker-compose restart user-service

# Remove all containers and volumes
docker-compose down -v

# Production deployment
docker-compose -f docker-compose.prod.yml up -d
```

## 🛠️ Troubleshooting

### Port Already in Use
```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Database Connection Failed
```bash
# Check PostgreSQL
docker-compose logs postgres

# Connect to database
docker exec -it intellidesk-postgres psql -U intellidesk -d intellidesk
```

### Service Not Registered in Eureka
```bash
# Check Eureka logs
docker-compose logs eureka-server

# Check service logs
docker-compose logs <service-name>

# Verify config server
curl http://localhost:8888/actuator/health
```

### OpenAI API Errors
```bash
# Check API key in .env
cat .env | grep OPENAI_API_KEY

# Test API key
curl https://api.openai.com/v1/models \
  -H "Authorization: Bearer $OPENAI_API_KEY"
```

## 🧪 Testing

### Backend Tests
```bash
cd <service-name>
mvn test

# Specific test
mvn test -Dtest=AuthServiceTest
```

### Frontend Tests
```bash
cd ui-frontend
npm test

# E2E tests
npm run e2e
```

### API Testing with cURL
```bash
# Get token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}' \
  | jq -r '.token')

# Use token
curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN"
```

## 📊 Monitoring

### Health Checks
```bash
# All services health
curl http://localhost:8080/actuator/health

# Specific service
curl http://localhost:8081/actuator/health
```

### Eureka Dashboard
Visit http://localhost:8761 to see:
- Registered services
- Instance status
- Uptime information

### Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f user-service

# Last 100 lines
docker-compose logs --tail=100 user-service
```

## 🔐 Security Checklist

- [ ] Change default passwords in `.env`
- [ ] Generate strong JWT secret (at least 256 bits)
- [ ] Configure proper OAuth2 credentials
- [ ] Set up HTTPS in production
- [ ] Enable Weaviate authentication in production
- [ ] Configure email credentials securely
- [ ] Review CORS settings in API Gateway
- [ ] Enable rate limiting
- [ ] Set up proper database backups

## 📦 Build & Deploy

### Maven Build
```bash
# Build all services
./build-all.sh

# Build specific service
cd user-service && mvn clean package -DskipTests
```

### Docker Build
```bash
# Build all images
docker-compose build

# Build specific image
docker-compose build user-service
```

### Production Deployment
```bash
# Build for production
./build-all.sh

# Deploy with production config
docker-compose -f docker-compose.prod.yml up -d

# Monitor deployment
docker-compose -f docker-compose.prod.yml logs -f
```

## 🔄 Updates & Maintenance

### Update Dependencies
```bash
# Backend
mvn versions:display-dependency-updates

# Frontend
cd ui-frontend && npm outdated
```

### Database Migrations
```bash
# Connect to database
docker exec -it intellidesk-postgres psql -U intellidesk -d intellidesk

# Run migrations
\i /path/to/migration.sql
```

### Backup Database
```bash
# Backup
docker exec intellidesk-postgres pg_dump -U intellidesk intellidesk > backup.sql

# Restore
docker exec -i intellidesk-postgres psql -U intellidesk intellidesk < backup.sql
```

## 📚 Additional Resources

- [Architecture Guide](PROJECT_STRUCTURE.md)
- [Setup Guide](SETUP.md)
- [Main README](README.md)

## 💡 Tips

1. **Start services in order**: config-server → eureka-server → other services
2. **Wait for health checks**: Services need time to register with Eureka
3. **Check logs frequently**: Most issues are visible in logs
4. **Use Postman/Insomnia**: Save API requests for easier testing
5. **Keep .env secure**: Never commit it to version control

## 🆘 Getting Help

If you encounter issues:
1. Check service logs: `docker-compose logs <service-name>`
2. Verify environment variables in `.env`
3. Ensure all required services are running
4. Check Eureka dashboard for service registration
5. Review this quick reference and SETUP.md

---

**Happy Coding! 🚀**
