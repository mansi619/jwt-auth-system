package com.onedao.auth.geolocation;

import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class CountryRestrictionConfig {

    private final Set<String> allowedCountries =
            Set.of("IN", "US", "GB", "CA", "AU");

    public Set<String> getAllowedCountries() {
        return allowedCountries;
    }

    public boolean isCountryAllowed(String countryCode) {

        return countryCode != null
                && allowedCountries.contains(
                        countryCode.toUpperCase());
    }
}