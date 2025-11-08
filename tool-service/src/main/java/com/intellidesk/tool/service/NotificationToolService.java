package com.intellidesk.tool.service;

import com.intellidesk.tool.dto.NotificationToolRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationToolService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${tools.notification.kafka-topic}")
    private String notificationTopic;

    public void sendNotification(NotificationToolRequest request) {
        try {
            // Create notification event
            Map<String, Object> notificationEvent = new HashMap<>();
            notificationEvent.put("recipient", request.getRecipient());
            notificationEvent.put("subject", request.getSubject());
            notificationEvent.put("template", request.getTemplate());
            notificationEvent.put("variables", request.getVariables());
            notificationEvent.put("type", request.getType());

            // Send to Kafka
            kafkaTemplate.send(notificationTopic, notificationEvent);
            
            log.info("Notification event sent to Kafka for recipient: {}", request.getRecipient());

        } catch (Exception e) {
            log.error("Error sending notification event to Kafka", e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }
}
