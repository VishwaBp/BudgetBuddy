package com.vishwanath.notification_events_service.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationProcessor {

    private final EmailNotificationService emailService;

    public NotificationProcessor(EmailNotificationService emailService) {
        this.emailService = emailService;
    }

    public void process(String message) {
        System.out.println("Processing message: " + message);
        emailService.sendEmail("recipient@example.com", "Notification Received", message);
    }
}

