package com.intellidesk.notification.consumer;

import com.intellidesk.notification.dto.NotificationEvent;
import com.intellidesk.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "${notification.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeNotificationEvent(NotificationEvent event) {
        log.info("Received notification event for: {}", event.getRecipient());
        
        try {
            if ("EMAIL".equalsIgnoreCase(event.getType())) {
                emailService.sendEmail(event);
            }
            // Add other notification types as needed
        } catch (Exception e) {
            log.error("Error processing notification event", e);
        }
    }
}
