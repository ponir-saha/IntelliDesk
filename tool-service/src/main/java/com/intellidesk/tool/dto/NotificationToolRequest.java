package com.intellidesk.tool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationToolRequest {
    private String recipient;
    private String subject;
    private String template;
    private Map<String, Object> variables;
    private String type;
}
