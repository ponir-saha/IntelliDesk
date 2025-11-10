#!/bin/bash

# IntelliDesk Monitoring Stack Startup Script

echo "🚀 Starting IntelliDesk with Full Monitoring Stack..."
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to check if service is healthy
check_service() {
    local service=$1
    local url=$2
    echo -n "Checking $service..."
    if curl -s -f "$url" > /dev/null 2>&1; then
        echo -e " ${GREEN}✓ UP${NC}"
        return 0
    else
        echo -e " ${YELLOW}⏳ Starting...${NC}"
        return 1
    fi
}

# Start all services
echo "Starting all services with docker-compose..."
docker-compose up -d

echo ""
echo "⏳ Waiting for services to start..."
sleep 10

echo ""
echo "📊 Service Status:"
echo "=================="

# Check infrastructure services
echo ""
echo "${BLUE}Infrastructure:${NC}"
check_service "PostgreSQL" "http://localhost:5432" || echo "  (Database is starting...)"
check_service "Weaviate" "http://localhost:8090/v1/.well-known/ready"
check_service "Kafka" "http://localhost:9092" || echo "  (Kafka is starting...)"
check_service "Zookeeper" "http://localhost:2181" || echo "  (Zookeeper is starting...)"

# Check core services
echo ""
echo "${BLUE}Core Services:${NC}"
check_service "Config Server" "http://localhost:8888/actuator/health"
check_service "Eureka Server" "http://localhost:8761/actuator/health"
check_service "API Gateway" "http://localhost:8080/actuator/health"
check_service "User Service" "http://localhost:8081/actuator/health"
check_service "RAG Service" "http://localhost:8083/actuator/health"
check_service "Notification Service" "http://localhost:8084/actuator/health"

# Check monitoring stack
echo ""
echo "${BLUE}Monitoring Stack:${NC}"
check_service "Elasticsearch" "http://localhost:9200/_cluster/health"
check_service "Kibana" "http://localhost:5601/api/status"
check_service "Logstash" "http://localhost:9600"
check_service "Prometheus" "http://localhost:9090/-/healthy"
check_service "Grafana" "http://localhost:3000/api/health"
check_service "Jaeger" "http://localhost:16686/"

echo ""
echo "================================"
echo "🎉 IntelliDesk is starting up!"
echo "================================"
echo ""
echo "📍 Access Points:"
echo ""
echo "  Application:"
echo "    • Frontend:        http://localhost:4200"
echo "    • API Gateway:     http://localhost:8080"
echo "    • Eureka Dashboard: http://localhost:8761"
echo ""
echo "  Monitoring & Observability:"
echo "    • Kibana (Logs):   http://localhost:5601"
echo "    • Grafana (Metrics): http://localhost:3000 (admin/admin)"
echo "    • Prometheus:      http://localhost:9090"
echo "    • Jaeger (Traces): http://localhost:16686"
echo ""
echo "  Metrics Endpoints:"
echo "    • User Service:    http://localhost:8081/actuator/prometheus"
echo "    • RAG Service:     http://localhost:8083/actuator/prometheus"
echo ""
echo "💡 Tips:"
echo "  • View all logs: docker-compose logs -f"
echo "  • View specific service: docker-compose logs -f rag-service"
echo "  • Check status: docker-compose ps"
echo "  • Stop all: docker-compose down"
echo ""
echo "📚 Documentation:"
echo "  • Monitoring Guide: MONITORING_STACK.md"
echo "  • WebSocket Guide: WEBSOCKET_INTEGRATION.md"
echo ""
