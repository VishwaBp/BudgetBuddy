package com.vishwanath.userservice.service.impl;

import com.vishwanath.userservice.model.Budget;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class BudgetManagementClient {
    private final RestTemplate restTemplate;

    public BudgetManagementClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Extract the token from Spring Security's SecurityContext.
     */
    private String getToken() {
        // Extract the token or credentials from SecurityContext
        return (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }


    /**
     * Call Budget Management Service to get all budgets for a given user.
     *
     * @return List of budgets
     */
    public ResponseEntity<String> getBudgetsForUser() {
        String token = getToken();

        String url = "http://localhost:8081/budgets";

        // Set Authorization Header with Bearer Token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make API call to Budget Management Service
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    /**
     * Call Budget Management Service to create a new budget for a user.
     *
     * @param budgetPayload The budget payload (JSON string)
     * @return The created budget
     */
    public ResponseEntity<String> createBudget(Budget budgetPayload) {
        String token = getToken();
        String url = "http://localhost:8081/budgets";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Budget> entity = new HttpEntity<>(budgetPayload, headers);
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

    }

}
