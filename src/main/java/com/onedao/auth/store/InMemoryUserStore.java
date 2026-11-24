
package com.onedao.auth.store;

import com.onedao.auth.entity.User;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryUserStore {

    private final Map<String, User> users =
            new ConcurrentHashMap<>();

    // Save user safely
    public synchronized void save(User user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User cannot be null");
        }

        String username = user.getUsername();
        String email = user.getEmail();

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username is required");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required");
        }

        if (users.containsKey(username)) {
            throw new IllegalArgumentException(
                    "Username already exists");
        }

        boolean emailExists = users.values().stream()
                .anyMatch(existingUser ->
                        existingUser.getEmail()
                                .equalsIgnoreCase(email));

        if (emailExists) {
            throw new IllegalArgumentException(
                    "Email already exists");
        }

        users.put(username, user);
    }

    // Find user by username
    public User findByUsername(String username) {

        if (username == null) {
            return null;
        }

        return users.get(username);
    }

    // Check duplicate username
    public boolean existsByUsername(String username) {

        return username != null && users.containsKey(username);
    }

    // Check duplicate email
    public boolean existsByEmail(String email) {

        if (email == null) {
            return false;
        }

        return users.values().stream()
                .anyMatch(user ->
                        user.getEmail().equalsIgnoreCase(email));
    }

    // Get total registered users
    public int getUserCount() {

        return users.size();
    }
}
