package com.vishwanath.userservice.service.impl;


import com.vishwanath.userservice.dto.UserDto;
import com.vishwanath.userservice.exception.UnauthorizedException;
import com.vishwanath.userservice.exception.UserNotFoundException;
import com.vishwanath.userservice.model.Budget;
import com.vishwanath.userservice.model.Token;
import com.vishwanath.userservice.model.User;
import com.vishwanath.userservice.repository.TokenRepository;
import com.vishwanath.userservice.repository.UserRepository;
import com.vishwanath.userservice.service.JwtService;
import com.vishwanath.userservice.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private BudgetManagementClient budgetClient;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, TokenRepository tokenRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Get all users and map them to UserDto.
     */
    @Override
    public List<UserDto> getAllUsers() {
        log.info("Retrieving all users...");
        // Extract the username from the token
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        // Validate the user performing the deletion
        User authenticatedUser = userRepository.findByUsername(usernameFromToken)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found with username: " + usernameFromToken));
        boolean isAdmin = authenticatedUser.getRole().name().equalsIgnoreCase("ADMIN");
        if(!isAdmin){
            log.error("Unauthorized attempt to get users Details  by {}",  usernameFromToken);
            throw new UnauthorizedException("You don't have permission for this action");

        }

        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Delete a user by ID with token validation.
     */
    @Transactional
    @Override
    public String deleteUser(Integer id) throws UnauthorizedException, UserNotFoundException {
        // Check if the user ID is provided
        if (id == null) {
            log.error("User ID is null. Cannot delete user.");
            throw new IllegalArgumentException("User ID cannot be null");
        }

        // Retrieve the user to be deleted by their ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        // Extract the username from the token
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();


        // Validate the user performing the deletion
        User authenticatedUser = userRepository.findByUsername(usernameFromToken)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found with username: " + usernameFromToken));

        // Allow deletion only if:
        // - Authenticated user is an ADMIN
        // - Authenticated user is deleting their own account
        boolean isAdmin = authenticatedUser.getRole().name().equalsIgnoreCase("ADMIN");
        boolean isOwnerDeletingSelf = usernameFromToken.equals(user.getUsername());

        if (!isAdmin && !isOwnerDeletingSelf) {
            log.error("Unauthorized attempt to delete user ID {} by {}", id, usernameFromToken);
            throw new UnauthorizedException("You don't have permission to delete this user");
        }

        // Delete associated tokens
        List<Token> userTokens = tokenRepository.findAllByUserId(id);
        if (!userTokens.isEmpty()) {
            log.info("Deleting {} tokens associated with user ID {}", userTokens.size(), id);
            tokenRepository.deleteAll(userTokens);
        }

        // Delete the user
        userRepository.deleteById(id);
        log.info("Deleted user with ID {} by {}", id, usernameFromToken);

        return "User deleted successfully!";

    }

    /**
     * Update existing user details.
     */
    @Transactional
    @Override
    public String updateUser(UserDto userDto) {
        if (userDto == null || userDto.getId() == null) {
            log.error("Invalid user data provided for update: {}", userDto);
            throw new IllegalArgumentException("Invalid user data. ID must not be null.");
        }

        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userDto.getId()));

        // Validate token and ensure only the authenticated user can update their account
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!usernameFromToken.equals(user.getUsername())) {
            log.error("Unauthorized attempt to update user ID {}", userDto.getId());
            throw new UnauthorizedException("You don't have permission to update this user");
        }

        // Update only non-empty fields from userDto
        if (StringUtils.hasText(userDto.getFirstName())) {
            user.setFirstName(userDto.getFirstName());
        }
        if (StringUtils.hasText(userDto.getLastName())) {
            user.setLastName(userDto.getLastName());
        }
        if (StringUtils.hasText(userDto.getUsername()) && !user.getUsername().equals(userDto.getUsername())) {
            if (userRepository.existsByUsername(userDto.getUsername())) {
                throw new IllegalArgumentException("Username is already taken");
            }
            user.setUsername(userDto.getUsername());
        }
        if (StringUtils.hasText(userDto.getPassword())) {
            String hashedPassword = passwordEncoder.encode(userDto.getPassword());
            user.setPassword(hashedPassword);
        }
        if (userDto.getRole() != null) {
            user.setRole(userDto.getRole());
        }

        userRepository.save(user);
        log.info("Updated user with ID {}", userDto.getId());
        return "User updated successfully!";
    }

    /**
     * Map User entity to UserDto.
     */
    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    /**
     * Get budgets for user by calling Budget Management Service.
     */
    public ResponseEntity<String> getBudgetsForUser() {
        return budgetClient.getBudgetsForUser();
    }

    /**
     * Create budget for user by calling Budget Management Service.
     */
    public ResponseEntity<String> createBudget( Budget budgetPayload) {
        return budgetClient.createBudget(budgetPayload);
    }



}
