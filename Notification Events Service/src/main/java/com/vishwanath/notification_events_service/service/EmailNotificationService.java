package com.vishwanath.notification_events_service.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final SendGrid sendGridClient;

    public EmailNotificationService(SendGrid sendGridClient) {
        this.sendGridClient = sendGridClient;
    }

    public void sendEmail(String recipient, String subject, String body) {
        Mail mail = new Mail(new Email("no-reply@my-app.com"), subject, new Email(recipient), new Content("text/plain", body));
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGridClient.api(request);
            System.out.println("Email sent with status: " + response.getStatusCode());
        } catch (Exception ex) {
            System.err.println("Error sending email: " + ex.getMessage());
        }
    }
}

