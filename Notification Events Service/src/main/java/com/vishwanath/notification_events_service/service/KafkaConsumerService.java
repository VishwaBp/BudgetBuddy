package com.vishwanath.notification_events_service.service;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final NotificationProcessor notificationProcessor;

    public KafkaConsumerService(NotificationProcessor notificationProcessor) {
        this.notificationProcessor = notificationProcessor;
    }

    @KafkaListener(topics = "NotificationEvents", groupId = "notification-group")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
        notificationProcessor.process(message);
    }
}

