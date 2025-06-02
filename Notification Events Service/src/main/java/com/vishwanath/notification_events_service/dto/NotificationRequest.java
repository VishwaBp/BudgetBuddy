package com.vishwanath.notification_events_service.dto;
import jakarta.validation.constraints.NotEmpty;

public class NotificationRequest {

    @NotEmpty(message = "Message cannot be empty")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
