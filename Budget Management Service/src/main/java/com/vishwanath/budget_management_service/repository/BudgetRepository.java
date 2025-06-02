package com.vishwanath.budget_management_service.repository;

import com.vishwanath.budget_management_service.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    // Query method to find all budgets for a specific user
    List<Budget> findByUsername(String username);

}

