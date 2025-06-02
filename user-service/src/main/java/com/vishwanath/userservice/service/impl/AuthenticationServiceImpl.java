package com.vishwanath.userservice.service.impl;


import com.vishwanath.userservice.exception.UserAlreadyExistsException;
import com.vishwanath.userservice.exception.UserNotFoundException;
import com.vishwanath.userservice.model.Token;
import com.vishwanath.userservice.model.User;
import com.vishwanath.userservice.repository.TokenRepository;
import com.vishwanath.userservice.repository.UserRepository;
import com.vishwanath.userservice.service.AuthenticationService;
import com.vishwanath.userservice.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final Validator validator; // Inject validator for manual model validation

    public AuthenticationServiceImpl(UserRepository repository,
                                     PasswordEncoder passwordEncoder,
                                     JwtService jwtService,
                                     TokenRepository tokenRepository,
                                     AuthenticationManager authenticationManager,
                                     Validator validator) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
        this.validator = validator; // Initialize the validator
    }

    @Override
    public String register(User request) {
        // Validate the request object with validator
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // Check if the username is already taken
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("A user with this username already exists.");
        }

        // Encode the password BEFORE saving it in the database
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(encodedPassword);

        // Save the user to the database
        repository.save(request);

        return "User registered successfully!";

    }

    @Override
    public String authenticate(User request) {
        try {
            // Authenticate using the AuthenticationManager
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Log successful authentication
            System.out.println("Authentication successful for user: " + request.getUsername());

            // Retrieve the user from the database
            User user = repository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("User not found with username: " + request.getUsername()));

            // Generate a JWT token
            String jwt = jwtService.generateToken(user);

            // Revoke all old tokens and save the new one
            revokeAllTokens(user);
            saveUserToken(jwt, user);

            return jwt;

        } catch (Exception ex) {
            // Log failed authentication
            System.err.println("Authentication failed: " + ex.getMessage());
            throw new RuntimeException("Authentication failed: " + ex.getMessage());
        }

    }

    // Helper method to save the user's JWT token
    private void saveUserToken(String jwt, User user) {
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    // Helper method to revoke all tokens for a user
    private void revokeAllTokens(User user) {
        List<Token> tokens = tokenRepository.findAllByUserId(user.getId());
        tokens.forEach(token -> token.setLoggedOut(true));
        tokenRepository.saveAll(tokens);
    }

}
