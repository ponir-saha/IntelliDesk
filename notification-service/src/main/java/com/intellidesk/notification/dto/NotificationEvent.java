package com.intellidesk.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String recipient;
    private String subject;
    private String template;
    private Map<String, Object> variables;
    private String type; // EMAIL, SMS, etc.
}
