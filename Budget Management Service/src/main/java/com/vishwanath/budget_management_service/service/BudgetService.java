package com.vishwanath.budget_management_service.service;

import com.vishwanath.budget_management_service.dto.BudgetRequest;
import com.vishwanath.budget_management_service.entity.Budget;

import java.util.List;

public interface BudgetService {

    Budget createBudget(BudgetRequest budgetRequest, String username);     // Create a new budget entry

    List<Budget> getUserBudgets(String username);  // Retrieve all budgets for a user

    Budget updateBudget(Integer budgetId, Budget budgetDetails, String username);  // Update budget details

    void deleteBudget(Integer budgetId, String username);   // Delete a budget entry
}
