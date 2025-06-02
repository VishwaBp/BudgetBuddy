package com.vishwanath.budget_management_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class BudgetRequest {
    private Double amount; // Amount for the budget
    private String category; // Category of the budget
    private String description; // Optional description

}
