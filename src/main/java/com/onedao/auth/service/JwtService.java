
package com.onedao.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.util.Date;
import java.util.Base64;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {

        byte[] keyBytes = Base64.getDecoder().decode(secret);

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    // Generate JWT Token
    public String generateToken(String username) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    // Extract username from JWT
    public String extractUsername(String token) {

        return getClaims(token).getSubject();
    }

    // Validate JWT Token
    public boolean isTokenValid(
            String token,
            String username) {

        try {

            Claims claims = getClaims(token);

            String tokenUsername = claims.getSubject();

            Date expiration = claims.getExpiration();

            return tokenUsername != null
                    && tokenUsername.equals(username)
                    && expiration != null
                    && expiration.after(new Date());

        } catch (JwtException | IllegalArgumentException e) {

            return false;
        }
    }

    // Extract JWT Claims
    private Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
