package com.intellidesk.tool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqlToolRequest {
    private String datasourceType; // "postgresql" or "mysql"
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private String query;
}
