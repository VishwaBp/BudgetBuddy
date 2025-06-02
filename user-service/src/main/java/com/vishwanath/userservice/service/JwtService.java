package com.vishwanath.userservice.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(UserDetails user);
    boolean isValid(String token, UserDetails user);
    String extractUsername(String token);
}
