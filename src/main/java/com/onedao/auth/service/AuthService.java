
package com.onedao.auth.service;

import com.onedao.auth.dto.AuthResponse;
import com.onedao.auth.dto.LoginRequest;
import com.onedao.auth.dto.RegisterRequest;
import com.onedao.auth.entity.User;
import com.onedao.auth.enums.UserRole;
import com.onedao.auth.store.InMemoryUserStore;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final InMemoryUserStore userStore;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            InMemoryUserStore userStore,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userStore = userStore;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // User Registration
    public AuthResponse register(RegisterRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Registration request cannot be empty");
        }

        String username = request.getUsername();
        String email = request.getEmail();
        String password = request.getPassword();
        String userType = request.getUserType();

        // Validate username
        if (username == null
                || username.trim().length() < 8
                || username.trim().length() > 50) {

            throw new IllegalArgumentException(
                    "Username must be between 8 and 50 characters");
        }

        username = username.trim();

        // Validate email
        if (email == null
                || email.isBlank()
                || !email.contains("@")
                || !email.contains("_")) {

            throw new IllegalArgumentException(
                    "Email must contain @ and _");
        }

        email = email.trim().toLowerCase();

        // Validate password
        if (password == null
                || password.length() < 8
                || password.length() > 12
                || !password.matches(".*[A-Z].*")
                || !password.matches(".*[a-z].*")
                || !password.matches(".*\\d.*")
                || !password.matches(".*[^a-zA-Z0-9].*")) {

            throw new IllegalArgumentException(
                    "Password must be 8-12 characters and contain uppercase, lowercase, digit and special character");
        }

        // Check duplicate username
        if (userStore.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "Username already exists");
        }

        // Check duplicate email
        if (userStore.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email already exists");
        }

        // Public registration is restricted to standard users
        if (userType == null
                || !userType.trim().equalsIgnoreCase("STANDARD_USER")) {

            throw new IllegalArgumentException(
                    "Public registration is allowed only for STANDARD_USER");
        }

        UserRole role = UserRole.STANDARD_USER;

        // Encrypt password
        String encodedPassword = passwordEncoder.encode(password);

        // Create user
        User user = new User(
                username,
                email,
                encodedPassword,
                role
        );

        // Save user in memory
        userStore.save(user);

        // Generate JWT
        String token = jwtService.generateToken(user.getUsername());

        return new AuthResponse(
                token,
                "Registration Successful"
        );
    }

    // User Login
    public AuthResponse login(LoginRequest request) {

        if (request == null
                || request.getUsername() == null
                || request.getUsername().isBlank()
                || request.getPassword() == null
                || request.getPassword().isEmpty()) {

            throw new IllegalArgumentException(
                    "Username and password are required");
        }

        // Find user
        User user = userStore.findByUsername(
                request.getUsername().trim());

        if (user == null) {
            throw new IllegalArgumentException("Login Failed");
        }

        // Verify password
        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword());

        if (!passwordMatches) {
            throw new IllegalArgumentException("Login Failed");
        }

        // Generate JWT
        String token = jwtService.generateToken(user.getUsername());

        return new AuthResponse(
                token,
                "Login Successful"
        );
    }
}
