package com.vishwanath.budget_management_service.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final Key key;
    private final long jwtExpiration;

    /**
     * Constructor to initialize key and expiration from application.yml
     *
     * @param secret JWT secret key
     * @param expiration JWT expiration time in milliseconds
     */
    public JwtUtil(@Value("${spring.jwt.secret}") String secret,
                   @Value("${spring.jwt.expiration}") long expiration) {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes); // Create the signing key
        this.jwtExpiration = expiration;
    }

    /**
     * Extract the username from the JWT token.
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extract all claims from the JWT token.
     */
    public Claims extractClaims(String token) {
        // Use HS384 algorithm while validating
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generate a new JWT token for a user.
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setHeader(Map.of("typ", "JWT")) // Include typ for consistency
                .setClaims(Map.of()) // Add claims if needed
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key, SignatureAlgorithm.HS384) // Explicitly use HS384
                .compact();
    }

    /**
     * Validate the token.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Check if the token is expired.
     */
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}






