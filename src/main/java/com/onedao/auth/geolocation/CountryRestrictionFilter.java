
package com.onedao.auth.geolocation;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CountryRestrictionFilter implements Filter {

    private final GeoLocationService geoLocationService;
    private final CountryLookupService countryLookupService;
    private final CountryRestrictionConfig countryRestrictionConfig;

    public CountryRestrictionFilter(
            GeoLocationService geoLocationService,
            CountryLookupService countryLookupService,
            CountryRestrictionConfig countryRestrictionConfig) {

        this.geoLocationService = geoLocationService;
        this.countryLookupService = countryLookupService;
        this.countryRestrictionConfig = countryRestrictionConfig;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // Skip CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // Apply restriction only to Product APIs
        if (!path.equals("/api/products")
                && !path.startsWith("/api/products/")) {

            chain.doFilter(request, response);
            return;
        }

        // Identify client IP
        String ipAddress =
                geoLocationService.getClientIp(httpRequest);

        // Find client country
        String countryCode =
                countryLookupService.getCountryCode(ipAddress);

        // Allow local development requests
        if ("LOCAL".equals(countryCode)) {
            chain.doFilter(request, response);
            return;
        }

        // Block countries not in the allowed list
        if (!countryRestrictionConfig.isCountryAllowed(countryCode)) {

            httpResponse.setStatus(
                    HttpServletResponse.SC_FORBIDDEN);

            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");

            httpResponse.getWriter().write(
                    "{\"message\":\"Access denied for this country\"}");

            return;
        }

        chain.doFilter(request, response);
    }
}
