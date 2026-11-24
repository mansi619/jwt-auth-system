
package com.onedao.auth.store;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklistStore {

    private final Set<String> revokedTokens =
            ConcurrentHashMap.newKeySet();

    // Add token to blacklist
    public void revokeToken(String token) {

        if (token != null && !token.isBlank()) {
            revokedTokens.add(token);
        }
    }

    // Check whether token is revoked
    public boolean isRevoked(String token) {

        return token != null && revokedTokens.contains(token);
    }

    // Get number of revoked tokens
    public int getRevokedTokenCount() {

        return revokedTokens.size();
    }
}
