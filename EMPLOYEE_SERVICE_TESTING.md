# Employee Service Testing Guide

## Overview
The Employee Service provides role-based employee management with the following access control:
- **HR/ADMIN roles**: Full CRUD access to all employees
- **EMPLOYEE role**: Can only view and update their own profile (with restrictions)

## Service Details
- **Port**: 8086
- **Base URL**: http://localhost:8080/api/employees (via API Gateway)
- **Direct URL**: http://localhost:8086/api/employees (direct access)
- **Database**: PostgreSQL (shared `intellidesk` database)
- **Authentication**: JWT-based (HS384 algorithm)

## Prerequisites

### 1. Start the Services
```bash
cd /Users/ponirsaha/Documents/IntelliDesk
docker-compose up -d
```

### 2. Verify Service is Running
```bash
# Check service status
docker-compose ps employee-service

# Check service logs
docker-compose logs --tail=50 employee-service

# Verify Eureka registration
curl http://localhost:8761/
```

### 3. Get JWT Tokens

#### Login as HR User
```bash
# First, create an HR user via user-service
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "hr_admin",
    "email": "hr@intellidesk.com",
    "password": "Hr@123456",
    "firstName": "HR",
    "lastName": "Admin",
    "role": "HR"
  }'

# Login to get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "hr_admin",
    "password": "Hr@123456"
  }'

# Response:
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "type": "Bearer",
  "userId": 1,
  "username": "hr_admin",
  "email": "hr@intellidesk.com",
  "role": "HR"
}

# Save the token
export HR_TOKEN="eyJhbGciOiJIUzM4NCJ9..."
```

#### Login as Regular Employee
```bash
# Create an employee user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john.doe@intellidesk.com",
    "password": "Employee@123",
    "firstName": "John",
    "lastName": "Doe",
    "role": "EMPLOYEE"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "Employee@123"
  }'

# Save the token
export EMP_TOKEN="eyJhbGciOiJIUzM4NCJ9..."
```

## API Endpoints Testing

### 1. Create Employee (HR/ADMIN only)

```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Authorization: Bearer $HR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "EMP001",
    "userId": 2,
    "email": "john.doe@intellidesk.com",
    "firstName": "John",
    "lastName": "Doe",
    "middleName": "Michael",
    "phoneNumber": "+1234567890",
    "alternatePhone": "+0987654321",
    "dateOfBirth": "1990-05-15",
    "gender": "MALE",
    "department": "Engineering",
    "designation": "Senior Software Engineer",
    "joiningDate": "2023-01-15",
    "employmentType": "FULL_TIME",
    "status": "ACTIVE",
    "reportingManager": "Alice Johnson",
    "salary": 120000.00,
    "address": "123 Main St, Apt 4B, New York, NY 10001",
    "emergencyContactName": "Jane Doe",
    "emergencyContactPhone": "+1234567891",
    "emergencyContactRelation": "Spouse",
    "bankName": "Chase Bank",
    "bankAccountNumber": "1234567890",
    "bankIfscCode": "CHASE001",
    "skills": "Java, Spring Boot, Microservices, Docker, Kubernetes",
    "qualifications": "B.Tech Computer Science, M.S. Software Engineering",
    "certifications": "AWS Certified Solutions Architect, Oracle Certified Professional",
    "notes": "High performer, team lead for Project X"
  }'

# Expected Response (201 Created):
{
  "id": 1,
  "employeeId": "EMP001",
  "userId": 2,
  "email": "john.doe@intellidesk.com",
  "firstName": "John",
  "lastName": "Doe",
  "middleName": "Michael",
  "phoneNumber": "+1234567890",
  "alternatePhone": "+0987654321",
  "dateOfBirth": "1990-05-15",
  "gender": "MALE",
  "department": "Engineering",
  "designation": "Senior Software Engineer",
  "joiningDate": "2023-01-15",
  "employmentType": "FULL_TIME",
  "status": "ACTIVE",
  "reportingManager": "Alice Johnson",
  "salary": 120000.00,
  "address": "123 Main St, Apt 4B, New York, NY 10001",
  "emergencyContactName": "Jane Doe",
  "emergencyContactPhone": "+1234567891",
  "emergencyContactRelation": "Spouse",
  "bankName": "Chase Bank",
  "bankAccountNumber": "1234567890",
  "bankIfscCode": "CHASE001",
  "skills": "Java, Spring Boot, Microservices, Docker, Kubernetes",
  "qualifications": "B.Tech Computer Science, M.S. Software Engineering",
  "certifications": "AWS Certified Solutions Architect, Oracle Certified Professional",
  "notes": "High performer, team lead for Project X",
  "profileImageUrl": null,
  "createdAt": "2025-11-09T10:55:00",
  "updatedAt": "2025-11-09T10:55:00",
  "createdBy": "hr_admin",
  "updatedBy": "hr_admin"
}
```

### 2. Get All Employees (HR/ADMIN only)

```bash
curl -X GET http://localhost:8080/api/employees \
  -H "Authorization: Bearer $HR_TOKEN"

# Expected Response (200 OK):
[
  {
    "id": 1,
    "employeeId": "EMP001",
    "userId": 2,
    "email": "john.doe@intellidesk.com",
    "firstName": "John",
    "lastName": "Doe",
    "department": "Engineering",
    "designation": "Senior Software Engineer",
    "status": "ACTIVE",
    "salary": 120000.00,
    ...
  }
]
```

### 3. Get Employee by ID (HR/ADMIN only)

```bash
curl -X GET http://localhost:8080/api/employees/1 \
  -H "Authorization: Bearer $HR_TOKEN"

# Expected Response (200 OK): Full employee details
```

### 4. Search Employees (HR/ADMIN only)

```bash
# Search by name, email, or employee ID
curl -X GET "http://localhost:8080/api/employees/search?keyword=john" \
  -H "Authorization: Bearer $HR_TOKEN"

# Expected Response (200 OK): List of matching employees
```

### 5. Update Employee (HR/ADMIN only)

```bash
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Authorization: Bearer $HR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "EMP001",
    "userId": 2,
    "email": "john.doe@intellidesk.com",
    "firstName": "John",
    "lastName": "Doe",
    "middleName": "Michael",
    "phoneNumber": "+1234567890",
    "alternatePhone": "+0987654321",
    "dateOfBirth": "1990-05-15",
    "gender": "MALE",
    "department": "Engineering",
    "designation": "Lead Software Engineer",
    "joiningDate": "2023-01-15",
    "employmentType": "FULL_TIME",
    "status": "ACTIVE",
    "reportingManager": "Alice Johnson",
    "salary": 135000.00,
    "address": "123 Main St, Apt 4B, New York, NY 10001",
    "emergencyContactName": "Jane Doe",
    "emergencyContactPhone": "+1234567891",
    "emergencyContactRelation": "Spouse",
    "bankName": "Chase Bank",
    "bankAccountNumber": "1234567890",
    "bankIfscCode": "CHASE001",
    "skills": "Java, Spring Boot, Microservices, Docker, Kubernetes, AWS",
    "qualifications": "B.Tech Computer Science, M.S. Software Engineering",
    "certifications": "AWS Certified Solutions Architect, Oracle Certified Professional, Kubernetes CKAD",
    "notes": "Promoted to Lead Engineer"
  }'

# Expected Response (200 OK): Updated employee details
```

### 6. Update Employee Status (HR/ADMIN only)

```bash
# Change status to ON_LEAVE
curl -X PATCH "http://localhost:8080/api/employees/1/status?status=ON_LEAVE" \
  -H "Authorization: Bearer $HR_TOKEN"

# Expected Response (200 OK): Updated employee with new status

# Available status values:
# - ACTIVE
# - INACTIVE
# - ON_LEAVE
# - TERMINATED
# - RESIGNED
```

### 7. Delete Employee (HR/ADMIN only)

```bash
curl -X DELETE http://localhost:8080/api/employees/1 \
  -H "Authorization: Bearer $HR_TOKEN"

# Expected Response (204 No Content)
```

### 8. Get My Profile (Any Authenticated User)

```bash
# Employee viewing their own profile
curl -X GET http://localhost:8080/api/employees/my-profile \
  -H "Authorization: Bearer $EMP_TOKEN"

# Expected Response (200 OK):
# Returns limited view WITHOUT salary, bank details, and internal notes
{
  "id": 1,
  "employeeId": "EMP001",
  "userId": 2,
  "email": "john.doe@intellidesk.com",
  "firstName": "John",
  "lastName": "Doe",
  "middleName": "Michael",
  "phoneNumber": "+1234567890",
  "alternatePhone": "+0987654321",
  "dateOfBirth": "1990-05-15",
  "gender": "MALE",
  "department": "Engineering",
  "designation": "Senior Software Engineer",
  "joiningDate": "2023-01-15",
  "employmentType": "FULL_TIME",
  "status": "ACTIVE",
  "reportingManager": "Alice Johnson",
  "address": "123 Main St, Apt 4B, New York, NY 10001",
  "emergencyContactName": "Jane Doe",
  "emergencyContactPhone": "+1234567891",
  "emergencyContactRelation": "Spouse",
  "skills": "Java, Spring Boot, Microservices, Docker, Kubernetes",
  "qualifications": "B.Tech Computer Science, M.S. Software Engineering",
  "certifications": "AWS Certified Solutions Architect, Oracle Certified Professional",
  "profileImageUrl": null,
  "createdAt": "2025-11-09T10:55:00",
  "updatedAt": "2025-11-09T10:55:00"
}
# Note: salary, bankAccountNumber, bankIfscCode, notes are NOT included
```

### 9. Update My Profile (Any Authenticated User)

```bash
# Employee can update limited fields (not salary or bank details)
curl -X PATCH http://localhost:8080/api/employees/my-profile \
  -H "Authorization: Bearer $EMP_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "+1234567899",
    "alternatePhone": "+0987654322",
    "address": "456 New Address St, Apt 5C, New York, NY 10002",
    "emergencyContactName": "Jane Doe",
    "emergencyContactPhone": "+1234567892",
    "emergencyContactRelation": "Spouse",
    "skills": "Java, Spring Boot, Microservices, Docker, Kubernetes, React",
    "certifications": "AWS Certified Solutions Architect, Oracle Certified Professional, Docker Certified"
  }'

# Expected Response (200 OK): Updated profile (limited view)
# Note: Fields like salary, employeeId, userId, department, designation cannot be updated by employees
```

## Access Control Testing

### Test 1: Employee trying to access HR endpoints (should fail)

```bash
# Employee trying to get all employees
curl -X GET http://localhost:8080/api/employees \
  -H "Authorization: Bearer $EMP_TOKEN"

# Expected Response (403 Forbidden):
{
  "timestamp": "2025-11-09T10:55:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/employees"
}

# Employee trying to create an employee
curl -X POST http://localhost:8080/api/employees \
  -H "Authorization: Bearer $EMP_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "EMP002",
    "email": "test@test.com",
    ...
  }'

# Expected Response (403 Forbidden)

# Employee trying to delete an employee
curl -X DELETE http://localhost:8080/api/employees/1 \
  -H "Authorization: Bearer $EMP_TOKEN"

# Expected Response (403 Forbidden)
```

### Test 2: Accessing without token (should fail)

```bash
curl -X GET http://localhost:8080/api/employees

# Expected Response (401 Unauthorized)

curl -X GET http://localhost:8080/api/employees/my-profile

# Expected Response (401 Unauthorized)
```

### Test 3: HR can access everything

```bash
# HR getting all employees
curl -X GET http://localhost:8080/api/employees \
  -H "Authorization: Bearer $HR_TOKEN"

# Expected Response (200 OK): Full list with salary and bank details

# HR updating an employee
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Authorization: Bearer $HR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ ... }'

# Expected Response (200 OK): Updated employee
```

## Monitoring and Metrics

### Actuator Endpoints
```bash
# Health check
curl http://localhost:8086/actuator/health

# Prometheus metrics
curl http://localhost:8086/actuator/prometheus

# Info
curl http://localhost:8086/actuator/info
```

### Prometheus Metrics
Access Prometheus UI: http://localhost:9090

Query examples:
- `http_server_requests_seconds_count{application="employee-service"}`
- `jvm_memory_used_bytes{application="employee-service"}`
- `process_cpu_usage{application="employee-service"}`

### Grafana Dashboards
Access Grafana: http://localhost:3000 (admin/admin)

Create dashboards with:
- Employee Service Response Times
- Request Count by Endpoint
- Error Rate
- JVM Memory Usage
- Database Connection Pool

### Jaeger Tracing
Access Jaeger UI: http://localhost:16686

Search for:
- Service: employee-service
- Operation: GET /api/employees
- Traces with duration > 100ms

### Elasticsearch Logs
Access Kibana: http://localhost:5601

Create index pattern: `logstash-*`

Search queries:
- `application:employee-service AND level:ERROR`
- `application:employee-service AND message:*Employee*`

## Database Verification

```bash
# Connect to PostgreSQL
docker exec -it intellidesk-postgres psql -U intellidesk -d intellidesk

# Check employees table
SELECT * FROM employees;

# Check employee by ID
SELECT * FROM employees WHERE id = 1;

# Check employees by department
SELECT * FROM employees WHERE department = 'Engineering';

# Check active employees
SELECT * FROM employees WHERE status = 'ACTIVE';
```

## Eureka Service Discovery

Check Eureka Dashboard: http://localhost:8761

You should see:
- **EMPLOYEE-SERVICE** registered
- Status: UP
- Port: 8086

## Common Issues and Solutions

### 1. Service not starting
```bash
# Check logs
docker-compose logs employee-service

# Common issues:
# - Config server not available: Wait for config-server to be healthy
# - Database connection: Check postgres container is running
# - Eureka registration: Check eureka-server is running
```

### 2. 401 Unauthorized
- Ensure JWT token is valid and not expired
- Check Authorization header format: `Bearer <token>`
- Verify token is from the correct environment

### 3. 403 Forbidden
- Check user role in JWT token
- Verify endpoint access requirements
- Employee users cannot access HR endpoints

### 4. 404 Not Found
- Verify service is registered in Eureka
- Check API Gateway is routing correctly
- Ensure endpoint path is correct

## Load Testing

### Using Apache Bench
```bash
# Test get all employees (with HR token)
ab -n 1000 -c 10 -H "Authorization: Bearer $HR_TOKEN" \
  http://localhost:8080/api/employees

# Test get my profile (with employee token)
ab -n 1000 -c 10 -H "Authorization: Bearer $EMP_TOKEN" \
  http://localhost:8080/api/employees/my-profile
```

### Using wrk
```bash
# Install wrk
brew install wrk

# Load test
wrk -t12 -c400 -d30s -H "Authorization: Bearer $HR_TOKEN" \
  http://localhost:8080/api/employees
```

## Security Testing

### Test JWT Validation
```bash
# Invalid token
curl -X GET http://localhost:8080/api/employees \
  -H "Authorization: Bearer invalid_token"
# Expected: 401 Unauthorized

# Expired token
curl -X GET http://localhost:8080/api/employees \
  -H "Authorization: Bearer expired_token"
# Expected: 401 Unauthorized

# No Bearer prefix
curl -X GET http://localhost:8080/api/employees \
  -H "Authorization: $HR_TOKEN"
# Expected: 401 Unauthorized
```

### Test Input Validation
```bash
# Invalid email format
curl -X POST http://localhost:8080/api/employees \
  -H "Authorization: Bearer $HR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "EMP002",
    "email": "invalid-email",
    ...
  }'
# Expected: 400 Bad Request with validation error

# Past date validation
curl -X POST http://localhost:8080/api/employees \
  -H "Authorization: Bearer $HR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "EMP002",
    "dateOfBirth": "2030-01-01",
    ...
  }'
# Expected: 400 Bad Request
```

## Summary

The Employee Service is now fully operational with:

✅ Role-based access control (HR/ADMIN vs EMPLOYEE)
✅ JWT authentication
✅ Full CRUD operations for HR/ADMIN
✅ Limited profile access for employees
✅ Input validation
✅ Exception handling
✅ Database persistence
✅ Service discovery (Eureka)
✅ Centralized configuration
✅ Prometheus metrics
✅ Jaeger distributed tracing
✅ ELK logging
✅ API Gateway routing

**Service Port**: 8086
**API Gateway**: http://localhost:8080/api/employees
**Monitoring**: Prometheus, Grafana, Jaeger, Kibana
