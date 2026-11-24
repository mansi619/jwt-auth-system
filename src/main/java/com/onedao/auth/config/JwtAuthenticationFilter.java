
package com.onedao.auth.config;

import com.onedao.auth.entity.User;
import com.onedao.auth.service.JwtService;
import com.onedao.auth.store.InMemoryUserStore;
import com.onedao.auth.store.TokenBlacklistStore;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final InMemoryUserStore userStore;
    private final TokenBlacklistStore tokenBlacklistStore;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            InMemoryUserStore userStore,
            TokenBlacklistStore tokenBlacklistStore) {

        this.jwtService = jwtService;
        this.userStore = userStore;
        this.tokenBlacklistStore = tokenBlacklistStore;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        if (token.isEmpty()) {
            sendUnauthorizedResponse(response, "Invalid token");
            return;
        }

        if (tokenBlacklistStore.isRevoked(token)) {
            sendUnauthorizedResponse(response, "Token has been revoked");
            return;
        }

        try {
            String username = jwtService.extractUsername(token);

            if (username == null ||
                    !jwtService.isTokenValid(token, username)) {

                sendUnauthorizedResponse(response, "Invalid or expired token");
                return;
            }

            if (SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                User user = userStore.findByUsername(username);

                if (user == null) {
                    sendUnauthorizedResponse(response, "User not found");
                    return;
                }

                var authorities = List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().name()));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                authorities);

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }

        } catch (Exception e) {

            SecurityContextHolder.clearContext();

            sendUnauthorizedResponse(response, "Invalid or expired token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorizedResponse(
            HttpServletResponse response,
            String message) throws IOException {

        SecurityContextHolder.clearContext();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                "{\"message\":\"" + message + "\"}");
    }
}
