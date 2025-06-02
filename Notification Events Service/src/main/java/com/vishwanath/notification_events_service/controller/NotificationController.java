package com.vishwanath.notification_events_service.controller;

import com.vishwanath.notification_events_service.dto.NotificationRequest;
import com.vishwanath.notification_events_service.service.KafkaProducerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final KafkaProducerService kafkaProducerService;

    public NotificationController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping
    public String sendNotification(@RequestBody NotificationRequest request) {
        kafkaProducerService.sendMessage("NotificationEvents", request.getMessage());
        return "Notification sent!";
    }

}

