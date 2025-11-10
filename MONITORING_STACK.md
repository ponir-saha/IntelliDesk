# Monitoring, Logging, and Tracing Stack

## Overview

IntelliDesk now includes a complete observability stack:
- **ELK Stack** (Elasticsearch, Logstash, Kibana) - Centralized logging
- **Prometheus** - Metrics collection
- **Grafana** - Metrics visualization and dashboards
- **Jaeger** - Distributed tracing

## Services and Ports

| Service | Port | URL | Purpose |
|---------|------|-----|---------|
| **Elasticsearch** | 9200 | http://localhost:9200 | Log storage |
| **Logstash** | 5000 | - | Log aggregation |
| **Kibana** | 5601 | http://localhost:5601 | Log visualization |
| **Prometheus** | 9090 | http://localhost:9090 | Metrics collection |
| **Grafana** | 3000 | http://localhost:3000 | Dashboards (admin/admin) |
| **Jaeger UI** | 16686 | http://localhost:16686 | Trace visualization |

## Quick Start

### 1. Start All Services
```bash
cd /Users/ponirsaha/Documents/IntelliDesk
docker-compose up -d
```

### 2. Verify Services are Running
```bash
docker-compose ps
```

### 3. Access UIs

**Kibana (Logs)**
- URL: http://localhost:5601
- First time setup:
  1. Go to Management → Stack Management → Index Patterns
  2. Create index pattern: `intellidesk-logs-*`
  3. Select `@timestamp` as time field
  4. Go to Discover to view logs

**Grafana (Metrics)**
- URL: http://localhost:3000
- Username: `admin`
- Password: `admin`
- Datasources are pre-configured (Prometheus, Elasticsearch, Jaeger)

**Prometheus**
- URL: http://localhost:9090
- View targets: http://localhost:9090/targets
- All microservices should be UP

**Jaeger (Traces)**
- URL: http://localhost:16686
- Select service from dropdown to view traces

## Configuration Details

### Prometheus Metrics

All Spring Boot services expose Prometheus metrics at `/actuator/prometheus`.

**Available Metrics:**
- `http_server_requests_seconds` - HTTP request duration
- `jvm_memory_used_bytes` - JVM memory usage
- `jvm_threads_live` - Thread count
- `process_cpu_usage` - CPU usage
- Custom business metrics

**Scrape Interval:** 15 seconds

**Monitored Services:**
- eureka-server:8761
- config-server:8888
- api-gateway:8080
- user-service:8081
- rag-service:8083
- notification-service:8084
- tool-service:8085

### Logstash Configuration

**Input:** TCP/UDP on port 5000 (JSON format)

**Pipeline:**
1. Receives logs from Spring Boot applications
2. Parses JSON structure
3. Extracts timestamp and log level
4. Stores in Elasticsearch with daily indices: `intellidesk-logs-YYYY.MM.DD`

**To send logs to Logstash**, update your Spring Boot `application.yml`:

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n"
  level:
    root: INFO
    com.intellidesk: DEBUG

# Optional: Add Logstash appender
# Requires logstash-logback-encoder dependency
```

### Jaeger Tracing

**Configuration in application.yml:**

```yaml
management:
  tracing:
    sampling:
      probability: 1.0  # Sample 100% of traces (adjust for production)
  zipkin:
    tracing:
      endpoint: http://jaeger:9411/api/v2/spans
```

**Trace Propagation:**
- Automatic trace context propagation across microservices
- Uses Brave/Zipkin format
- Sent to Jaeger via Zipkin-compatible endpoint

### Actuator Endpoints

All services expose the following actuator endpoints:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

**Available Endpoints:**
- `/actuator/health` - Service health status
- `/actuator/info` - Service information
- `/actuator/metrics` - Available metrics
- `/actuator/prometheus` - Prometheus-formatted metrics

## Grafana Dashboards

### Pre-configured Datasources
1. **Prometheus** (Default) - Metrics from all services
2. **Elasticsearch** - Logs from Logstash
3. **Jaeger** - Distributed traces

### Creating Dashboards

**1. Spring Boot Dashboard:**
```
1. Go to Dashboards → New Dashboard
2. Add Panel → Select Prometheus datasource
3. Sample queries:
   - Request rate: rate(http_server_requests_seconds_count[5m])
   - Error rate: rate(http_server_requests_seconds_count{status=~"5.."}[5m])
   - Response time: histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
   - JVM Memory: jvm_memory_used_bytes{area="heap"}
```

**2. Service Health Dashboard:**
```
Query: up{job=~".*-service"}
Visualization: Stat panel showing 1 (UP) or 0 (DOWN)
```

**3. API Gateway Dashboard:**
```
- Total requests: sum(rate(http_server_requests_seconds_count[5m]))
- Requests by endpoint: sum by (uri) (rate(http_server_requests_seconds_count[5m]))
- Success rate: (sum(rate(http_server_requests_seconds_count{status=~"2.."}[5m])) / sum(rate(http_server_requests_seconds_count[5m]))) * 100
```

### Import Community Dashboards

Grafana has many pre-built dashboards:

1. Go to Dashboards → Import
2. Enter dashboard ID:
   - **4701** - JVM Dashboard
   - **11159** - Spring Boot Statistics
   - **12856** - Spring Boot Observability
3. Select Prometheus datasource
4. Import

## Kibana Log Analysis

### Creating Searches

**1. Error Logs:**
```
level: ERROR OR level: WARN
```

**2. Service-Specific Logs:**
```
logger: "com.intellidesk.user.*"
```

**3. Upload Progress Logs:**
```
message: "Progress:" OR message: "segments processed"
```

**4. Time-based Analysis:**
- Use the time picker to select date range
- Create visualizations for error trends
- Set up alerts for critical errors

### Kibana Visualizations

**1. Error Rate Over Time:**
- Visualization: Line chart
- Metrics: Count of documents
- Filter: `level: ERROR`
- Buckets: Date Histogram on `@timestamp`

**2. Logs by Service:**
- Visualization: Pie chart
- Buckets: Terms aggregation on `logger` field

**3. Top Error Messages:**
- Visualization: Data table
- Buckets: Terms on `message` field
- Filter: `level: ERROR`

## Jaeger Distributed Tracing

### Viewing Traces

1. **Service View:**
   - Select service from dropdown
   - View service dependencies
   - See request flow through microservices

2. **Trace Timeline:**
   - Click on any trace to see detailed timeline
   - Spans show time spent in each service
   - Identify slow operations

3. **Finding Issues:**
   - Sort by duration to find slow requests
   - Filter by tags (e.g., `error=true`)
   - Compare traces to identify anomalies

### Understanding Traces

**Trace Structure:**
```
API Gateway (100ms)
  └─> User Service (30ms)
      ├─> Database Query (20ms)
      └─> JWT Validation (10ms)
  └─> RAG Service (50ms)
      ├─> Weaviate Query (30ms)
      └─> OpenAI API (20ms)
```

**Key Metrics:**
- Total trace duration
- Per-service latency
- Number of spans
- Error spans (highlighted in red)

## Production Considerations

### Resource Requirements

**Minimum:**
- Elasticsearch: 1GB RAM
- Logstash: 512MB RAM
- Kibana: 512MB RAM
- Prometheus: 512MB RAM
- Grafana: 256MB RAM
- Jaeger: 512MB RAM

**Recommended:**
- Elasticsearch: 2-4GB RAM
- Logstash: 1GB RAM
- Others: 512MB-1GB RAM

### Security

**1. Enable Authentication:**

Update `docker-compose.yml`:
```yaml
elasticsearch:
  environment:
    - xpack.security.enabled=true
    - ELASTIC_PASSWORD=your-secure-password
```

**2. Change Default Passwords:**
```yaml
grafana:
  environment:
    - GF_SECURITY_ADMIN_PASSWORD=your-secure-password
```

**3. Restrict Access:**
- Use reverse proxy (Nginx/Traefik)
- Enable HTTPS
- Configure firewall rules

### Data Retention

**Elasticsearch Indices:**

Create Index Lifecycle Management (ILM) policy:
```bash
# Keep logs for 30 days
curl -X PUT "localhost:9200/_ilm/policy/intellidesk-logs-policy" -H 'Content-Type: application/json' -d'
{
  "policy": {
    "phases": {
      "hot": {
        "actions": {
          "rollover": {
            "max_age": "1d",
            "max_size": "50GB"
          }
        }
      },
      "delete": {
        "min_age": "30d",
        "actions": {
          "delete": {}
        }
      }
    }
  }
}'
```

**Prometheus Data:**
```yaml
prometheus:
  command:
    - '--storage.tsdb.retention.time=30d'
    - '--storage.tsdb.retention.size=50GB'
```

### Performance Tuning

**Elasticsearch:**
```yaml
environment:
  - "ES_JAVA_OPTS=-Xms2g -Xmx2g"  # Increase heap size
  - bootstrap.memory_lock=true
```

**Prometheus:**
```yaml
command:
  - '--storage.tsdb.min-block-duration=2h'
  - '--storage.tsdb.max-block-duration=2h'
  - '--query.max-samples=50000000'
```

**Logstash:**
```yaml
environment:
  - "LS_JAVA_OPTS=-Xmx1g -Xms1g"
  - pipeline.workers=4
  - pipeline.batch.size=125
```

## Troubleshooting

### Elasticsearch Not Starting

**Issue:** Out of memory
```bash
# Increase memory
docker-compose up -d elasticsearch
docker logs intellidesk-elasticsearch
```

**Solution:**
```yaml
environment:
  - "ES_JAVA_OPTS=-Xms512m -Xmx512m"  # Reduce if needed
```

### Prometheus Not Scraping Metrics

**Check targets:**
```bash
# Visit: http://localhost:9090/targets
# All targets should show "UP"
```

**If DOWN:**
1. Verify service is running: `docker-compose ps`
2. Check actuator endpoint: `curl http://localhost:8081/actuator/prometheus`
3. Verify network connectivity between containers

### Logs Not Appearing in Kibana

**1. Check Logstash:**
```bash
docker-compose logs logstash
```

**2. Verify Elasticsearch indices:**
```bash
curl http://localhost:9200/_cat/indices?v
```

**3. Check Kibana index pattern:**
- Management → Index Patterns
- Create pattern: `intellidesk-logs-*`

### Jaeger No Traces

**1. Verify tracing configuration in application.yml:**
```yaml
management:
  tracing:
    sampling:
      probability: 1.0
  zipkin:
    tracing:
      endpoint: http://jaeger:9411/api/v2/spans
```

**2. Check dependencies in pom.xml:**
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```

**3. Restart services:**
```bash
docker-compose restart user-service rag-service
```

## Useful Commands

### View All Logs
```bash
docker-compose logs -f
```

### View Specific Service
```bash
docker-compose logs -f rag-service
docker-compose logs -f elasticsearch
```

### Check Resource Usage
```bash
docker stats
```

### Clean Up Old Data
```bash
# Remove old Elasticsearch indices
curl -X DELETE "localhost:9200/intellidesk-logs-2025.01.*"

# Clean Prometheus data
docker-compose stop prometheus
docker volume rm intellidesk_prometheus-data
docker-compose up -d prometheus
```

### Backup Grafana Dashboards
```bash
docker exec intellidesk-grafana grafana-cli admin reset-admin-password newpassword
```

## Monitoring Checklist

- [ ] All services showing UP in Prometheus targets
- [ ] Logs appearing in Kibana
- [ ] Grafana dashboards displaying metrics
- [ ] Traces visible in Jaeger
- [ ] Disk space adequate for log retention
- [ ] Memory usage within limits
- [ ] No error logs in monitoring services
- [ ] Alerts configured for critical metrics

## References

- Elasticsearch: https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html
- Kibana: https://www.elastic.co/guide/en/kibana/current/index.html
- Prometheus: https://prometheus.io/docs/introduction/overview/
- Grafana: https://grafana.com/docs/grafana/latest/
- Jaeger: https://www.jaegertracing.io/docs/
- Micrometer: https://micrometer.io/docs
