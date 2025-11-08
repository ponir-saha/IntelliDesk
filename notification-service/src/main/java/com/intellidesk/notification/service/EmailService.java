package com.intellidesk.notification.service;

import com.intellidesk.notification.dto.NotificationEvent;
import com.intellidesk.notification.entity.Notification;
import com.intellidesk.notification.entity.NotificationStatus;
import com.intellidesk.notification.entity.NotificationType;
import com.intellidesk.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final NotificationRepository notificationRepository;

    public void sendEmail(NotificationEvent event) {
        Notification notification = new Notification();
        notification.setRecipient(event.getRecipient());
        notification.setSubject(event.getSubject());
        notification.setType(NotificationType.EMAIL);
        notification.setStatus(NotificationStatus.PENDING);

        try {
            String htmlContent = processTemplate(event.getTemplate(), event.getVariables());
            notification.setContent(htmlContent);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(event.getRecipient());
            helper.setSubject(event.getSubject());
            helper.setText(htmlContent, true);

            mailSender.send(message);

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            
            log.info("Email sent successfully to: {}", event.getRecipient());

        } catch (MessagingException e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            log.error("Failed to send email to: {}", event.getRecipient(), e);
        }

        notificationRepository.save(notification);
    }

    private String processTemplate(String templateName, java.util.Map<String, Object> variables) {
        Context context = new Context();
        if (variables != null) {
            variables.forEach(context::setVariable);
        }
        return templateEngine.process(templateName, context);
    }
}
