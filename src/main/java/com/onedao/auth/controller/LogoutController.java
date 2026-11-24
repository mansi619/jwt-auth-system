
package com.onedao.auth.controller;

import com.onedao.auth.store.TokenBlacklistStore;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LogoutController {

    private final TokenBlacklistStore tokenBlacklistStore;

    public LogoutController(TokenBlacklistStore tokenBlacklistStore) {
        this.tokenBlacklistStore = tokenBlacklistStore;
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Authorization token is missing"
                    ));
        }

        String token = authHeader.substring(7).trim();

        if (token.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Invalid token"
                    ));
        }

        if (tokenBlacklistStore.isRevoked(token)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Token has already been revoked"
                    ));
        }

        tokenBlacklistStore.revokeToken(token);

        return ResponseEntity.ok(
                Map.of("message", "Logout Successful")
        );
    }
}
