package com.vishwanath.userservice.controller;

import com.vishwanath.userservice.dto.UserDto;
import com.vishwanath.userservice.exception.UnauthorizedException;
import com.vishwanath.userservice.exception.UserNotFoundException;
import com.vishwanath.userservice.model.Budget;
import com.vishwanath.userservice.service.impl.BudgetManagementClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import com.vishwanath.userservice.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.vishwanath.userservice.dto.UserDto;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final BudgetManagementClient budgetManagementClient;

    public UserController(UserService userService, BudgetManagementClient budgetManagementClient) {
        this.userService = userService;
        this.budgetManagementClient = budgetManagementClient;
    }

    /**
     * Get all user details.
     */
    @GetMapping("/getAllUserDetails")
    public ResponseEntity<List<UserDto>> getAllUserDetails() {
        List<UserDto> allUsers = userService.getAllUsers();
        if (allUsers.isEmpty()) {
            return ResponseEntity.noContent().build(); // Returns 204 No Content if no users are found
        }
        return ResponseEntity.ok(allUsers); // Returns 200 OK with the user list
    }

    /**
     * Update user details.
     */
    @PutMapping("/updateDetails")
    public ResponseEntity<String> updateDetails(@RequestBody UserDto userDto) {
        try {
            if (userDto.getId() == null) {
                return ResponseEntity.badRequest().body("User ID is required for updating user details");
            }

            // Extract the JWT token (removing the "Bearer " prefix)


            // Update the user
            String response = userService.updateUser(userDto);
            return ResponseEntity.ok(response); // Returns 200 OK if successful

        } catch (UnauthorizedException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // Returns 403 Forbidden if unauthorized

        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Returns 404 Not Found if the user does not exist

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Returns 400 Bad Request for validation errors
        }
    }

    /**
     * Delete a user.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body("User ID is required for deletion");
            }
            // Delete the user
            String response = userService.deleteUser(id);
            return ResponseEntity.ok(response); // Returns 200 OK if successful

        } catch (UnauthorizedException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // Returns 403 Forbidden if unauthorized

        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Returns 404 Not Found if the user does not exist

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Returns 400 Bad Request for validation errors
        }
    }

    /**
     * Helper method to extract token from Authorization header.
     *
     * @param authHeader The Authorization header containing the JWT token.
     * @return The extracted token without the "Bearer " prefix.
     */
    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }
        return authHeader.substring(7); // Removes "Bearer " prefix
    }

    /**
     * Fetch budgets for the currently authenticated user.
     * @return List of budgets
     */
    @GetMapping("/budgets")
    public ResponseEntity<String> getBudgetsForUser( ){
        return budgetManagementClient.getBudgetsForUser( );
    }

    /**
     * Create a new budget for the user.
     * @param budgetPayload Budget request payload (JSON string)
     * @return Created budget
     */
    @PostMapping("/budget")
    public ResponseEntity<String> createBudgetForUser(
            @RequestBody Budget budgetPayload) {
        return budgetManagementClient.createBudget(budgetPayload);
    }

}


