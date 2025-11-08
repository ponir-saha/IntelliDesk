package com.intellidesk.tool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiToolResponse {
    private boolean success;
    private Integer statusCode;
    private String message;
    private Object data;
}
