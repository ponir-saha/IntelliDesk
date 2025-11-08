#!/bin/bash

# Build script for all IntelliDesk services

echo "Building IntelliDesk Microservices..."

# Build common-lib first (shared dependency)
echo "Building common-lib..."
cd common-lib && mvn clean install -DskipTests && cd ..

# Build all Spring Boot services
services=("config-server" "eureka-server" "api-gateway" "user-service" "notification-service" "rag-service" "tool-service")

for service in "${services[@]}"
do
    echo "Building $service..."
    cd "$service" && mvn clean package -DskipTests && cd ..
done

# Build Angular frontend
echo "Building ui-frontend..."
cd ui-frontend && npm install && npm run build && cd ..

echo "All services built successfully!"
