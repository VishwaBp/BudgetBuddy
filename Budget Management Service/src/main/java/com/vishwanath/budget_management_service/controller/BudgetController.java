package com.vishwanath.budget_management_service.controller;

import com.vishwanath.budget_management_service.dto.BudgetRequest;
import com.vishwanath.budget_management_service.entity.Budget;
import com.vishwanath.budget_management_service.entity.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.vishwanath.budget_management_service.service.BudgetService;

import java.util.List;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;
    String userName = getAuthenticatedUsername();
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // Safely retrieve the username from UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }

        return null; // Not authenticated
    }

    /**
         * Create a new budget for a user.
         */
    @PostMapping
    public ResponseEntity<Budget> createBudget(@RequestBody BudgetRequest budgetRequest
           ) {


        Budget createdBudget = budgetService.createBudget( budgetRequest
                ,userName);
        return ResponseEntity.ok(createdBudget);
    }


    /**
     * Get all budgets for a specific user.
     */
    @GetMapping
    public ResponseEntity<List<Budget>> getUserBudgets() {

        List<Budget> budgets = budgetService.getUserBudgets(userName);
        return ResponseEntity.ok(budgets);
    }


    /**
     * Update a budget.
     */
    @PutMapping("/{budgetId}")
    public ResponseEntity<Budget> updateBudget(
            @PathVariable Integer budgetId,
            @RequestBody Budget budget) {

        Budget updatedBudget = budgetService.updateBudget(budgetId, budget, userName);
        return ResponseEntity.ok(updatedBudget);
    }


    /**
     * Delete a budget by its ID.
     */
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Integer budgetId) {

        budgetService.deleteBudget(budgetId, userName);
        return ResponseEntity.noContent().build();
    }

}

