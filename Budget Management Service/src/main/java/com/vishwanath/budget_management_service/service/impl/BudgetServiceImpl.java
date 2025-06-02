package com.vishwanath.budget_management_service.service.impl;


import com.vishwanath.budget_management_service.dto.BudgetRequest;
import com.vishwanath.budget_management_service.entity.Budget;
import com.vishwanath.budget_management_service.entity.User;
import com.vishwanath.budget_management_service.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.vishwanath.budget_management_service.repository.BudgetRepository;
import com.vishwanath.budget_management_service.service.BudgetService;

import java.util.List;

@Service
public
class BudgetServiceImpl implements BudgetService {



    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    // Constructor
    public BudgetServiceImpl(BudgetRepository budgetRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }



    @Override
    public Budget createBudget(BudgetRequest budgetRequest
            , String username
    ) {
        username = SecurityContextHolder.getContext().getAuthentication().getName();
        String finalUsername = username;
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + finalUsername));


        // Create a new Budget object and associate with the username
        Budget budget = new Budget();
        budget.setUsername(user.getUsername()); // Associate budget with the username
        budget.setAmount(budgetRequest.getAmount());
        budget.setCategory(budgetRequest.getCategory());
        budget.setDescription(budgetRequest.getDescription());

        // Save the budget entity to the database
        budgetRepository.save(budget);

        return budget;
    }


    @Override
    public List<Budget> getUserBudgets(String username) {
        username = SecurityContextHolder.getContext().getAuthentication().getName();
        String finalUsername = username;
        return budgetRepository.findByUsername(finalUsername);
    }

    @Override
    public Budget updateBudget(Integer budgetId, Budget budgetDetails, String username) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        // Ensure the budget belongs to the authenticated username
        if (!budget.getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to update this budget");
        }

        // Update budget fields (ensure that you use the correct entity fields)
        budget.setAmount(budgetDetails.getAmount());
        budget.setCategory(budgetDetails.getCategory());
        budget.setDescription(budgetDetails.getDescription());

        return budgetRepository.save(budget);
    }



    @Override
    public void deleteBudget(Integer budgetId, String username) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        // Ensure the budget belongs to the authenticated username
        if (!budget.getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to delete this budget");
        }

        // Delete the budget from the database
        budgetRepository.delete(budget);
    }


}

