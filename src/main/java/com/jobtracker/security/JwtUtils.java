package com.jobtracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/**
 * Utility class for JWT token lifecycle: Generation, Extraction, and
 * Validation.
 */
@Component
public class JwtUtils {

    // Should be moved to Environment Variables in Production for security
    @Value("${app.jwt.secret:defaultSecretKeyWithAtLeast32CharactersForHS256}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}") // 24 hours
    private int jwtExpirationMs;

    /**
     * Helper: Builds the cryptographic key from the secret string.
     * The secret string is expected to be Base64 encoded for robust key generation.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token for the given user details.
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the username (subject) from the given JWT token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Generic claim extractor for clean modular logic.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    /**
     * Validates a JWT token against user details.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
