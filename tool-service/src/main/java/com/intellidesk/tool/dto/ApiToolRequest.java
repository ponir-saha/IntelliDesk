package com.intellidesk.tool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiToolRequest {
    private String url;
    private String method; // GET, POST, PUT, DELETE
    private Map<String, String> headers;
    private Object body;
}
