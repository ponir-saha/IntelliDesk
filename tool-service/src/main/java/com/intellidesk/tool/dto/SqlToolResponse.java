package com.intellidesk.tool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqlToolResponse {
    private boolean success;
    private String message;
    private List<Map<String, Object>> results;
    private Integer rowCount;
}
