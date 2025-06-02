package com.vishwanath.userservice.service.impl;
import com.vishwanath.userservice.repository.TokenRepository;
import com.vishwanath.userservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;

@Service
public class JwtServiceImpl implements JwtService {

    private final TokenRepository tokenRepository;

    // Load the secret key from application properties
    @Value("${jwt.secret}")
    private String secretKey;

    // Move expiration time to a constant for better readability and maintainability
    private static final long EXPIRATION_TIME_MS = 86400000; // 24 hours

    private SecretKey signingKey; // Singleton object for thread-safe signing key

    public JwtServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public String generateToken(UserDetails user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(getSignKey(), SignatureAlgorithm.HS384) // Use reusable signing key
                .compact();
    }

    @Override
    public boolean isValid(String token, UserDetails user) {
        try {
            String username = extractUsername(token);

            // Check if the token matches the user and validity conditions
            boolean isTokenValid = tokenRepository.findByToken(token)
                    .map(t -> !t.isLoggedOut()) // Ensure the token is not logged out
                    .orElse(false);

            return username.equals(user.getUsername()) && !isTokenExpired(token) && isTokenValid;

        } catch (Exception e) {
            // Log and return false if an invalid or tampered token is detected
            return false;
        }
    }

    @Override
    public String extractUsername(String token) {
        try {
            return extractClaims(token).getSubject();
        } catch (Exception e) {
            // Log the exception and return null if token is invalid or tampered
            return null;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            // Handle invalid token case
            return true; // Consider expired if there's an issue parsing the token
        }
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT Token", e);
        }
    }

    // Reuse signing key for thread-safety and performance
    private SecretKey getSignKey() {
        if (signingKey == null) {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Decode the Base64 key
            signingKey = Keys.hmacShaKeyFor(keyBytes);
        }
        return signingKey;
    }

}
