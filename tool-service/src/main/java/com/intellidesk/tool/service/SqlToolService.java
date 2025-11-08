package com.intellidesk.tool.service;

import com.intellidesk.tool.dto.SqlToolRequest;
import com.intellidesk.tool.dto.SqlToolResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

@Service
@Slf4j
public class SqlToolService {

    private static final List<String> ALLOWED_OPERATIONS = List.of("SELECT");

    public SqlToolResponse executeQuery(SqlToolRequest request) {
        // Validate query
        if (!isQueryAllowed(request.getQuery())) {
            return new SqlToolResponse(false, "Only SELECT queries are allowed", null, 0);
        }

        String jdbcUrl = buildJdbcUrl(request);
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                jdbcUrl, request.getUsername(), request.getPassword());
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(request.getQuery())) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }

            log.info("SQL query executed successfully, returned {} rows", results.size());
            return new SqlToolResponse(true, "Query executed successfully", results, results.size());

        } catch (SQLException e) {
            log.error("Error executing SQL query", e);
            return new SqlToolResponse(false, "Error: " + e.getMessage(), null, 0);
        }
    }

    private String buildJdbcUrl(SqlToolRequest request) {
        String type = request.getDatasourceType().toLowerCase();
        String host = request.getHost();
        String port = request.getPort();
        String database = request.getDatabase();

        return switch (type) {
            case "postgresql" -> String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
            case "mysql" -> String.format("jdbc:mysql://%s:%s/%s", host, port, database);
            default -> throw new IllegalArgumentException("Unsupported datasource type: " + type);
        };
    }

    private boolean isQueryAllowed(String query) {
        String upperQuery = query.trim().toUpperCase();
        return ALLOWED_OPERATIONS.stream().anyMatch(upperQuery::startsWith);
    }
}
