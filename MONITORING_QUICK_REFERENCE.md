# Monitoring Stack - Quick Reference

## 🚀 Quick Start

```bash
# Start everything with monitoring
./start-with-monitoring.sh

# Or manually
docker-compose up -d
```

## 📊 Access URLs

| Service | URL | Credentials |
|---------|-----|-------------|
| **Kibana** | http://localhost:5601 | - |
| **Grafana** | http://localhost:3000 | admin/admin |
| **Prometheus** | http://localhost:9090 | - |
| **Jaeger** | http://localhost:16686 | - |
| **Elasticsearch** | http://localhost:9200 | - |

## 🔍 Common Tasks

### View Logs in Kibana
1. Open http://localhost:5601
2. Go to **Management** → **Stack Management** → **Index Patterns**
3. Create pattern: `intellidesk-logs-*`
4. Go to **Discover** to view logs

### Create Grafana Dashboard
1. Open http://localhost:3000 (admin/admin)
2. Go to **Dashboards** → **New Dashboard**
3. Add panel with Prometheus queries:
   ```
   rate(http_server_requests_seconds_count[5m])
   ```

### View Traces in Jaeger
1. Open http://localhost:16686
2. Select service from dropdown
3. Click **Find Traces**

### Check Prometheus Targets
- Visit: http://localhost:9090/targets
- All services should show **UP**

## 📈 Key Metrics

### Service Health
```
up{job="user-service"}
up{job="rag-service"}
```

### Request Rate
```
rate(http_server_requests_seconds_count[5m])
```

### Error Rate
```
rate(http_server_requests_seconds_count{status=~"5.."}[5m])
```

### Response Time (95th percentile)
```
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
```

### JVM Memory Usage
```
jvm_memory_used_bytes{area="heap"}
```

## 🔧 Troubleshooting

### Service Down in Prometheus
```bash
# Check if service is running
docker-compose ps

# Check actuator endpoint
curl http://localhost:8081/actuator/prometheus
```

### No Logs in Kibana
```bash
# Check Elasticsearch indices
curl http://localhost:9200/_cat/indices?v

# Check Logstash logs
docker-compose logs logstash
```

### No Traces in Jaeger
```bash
# Restart services to reconnect
docker-compose restart user-service rag-service
```

## 🛠️ Useful Commands

```bash
# View all logs
docker-compose logs -f

# View specific service
docker-compose logs -f rag-service

# Check resource usage
docker stats

# Restart monitoring stack
docker-compose restart elasticsearch kibana prometheus grafana jaeger

# Stop everything
docker-compose down

# Clean up volumes (WARNING: deletes data)
docker-compose down -v
```

## 📱 Health Check Endpoints

```bash
# User Service
curl http://localhost:8081/actuator/health

# RAG Service
curl http://localhost:8083/actuator/health

# Prometheus Metrics
curl http://localhost:8083/actuator/prometheus
```

## 🎯 Example Queries

### Prometheus

**Total Requests**
```
sum(rate(http_server_requests_seconds_count[5m]))
```

**Requests by Service**
```
sum by (job) (rate(http_server_requests_seconds_count[5m]))
```

**Average Response Time**
```
rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])
```

### Kibana

**Errors Only**
```
level: ERROR
```

**Upload Progress**
```
message: "Progress:" OR message: "segments processed"
```

**Last Hour Errors**
```
level: ERROR AND @timestamp: [now-1h TO now]
```

## 📞 Support

For detailed documentation, see:
- **MONITORING_STACK.md** - Complete monitoring guide
- **WEBSOCKET_INTEGRATION.md** - WebSocket progress tracking
- **README.md** - Project overview
