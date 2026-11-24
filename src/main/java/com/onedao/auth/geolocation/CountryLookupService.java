
package com.onedao.auth.geolocation;

import java.net.InetAddress;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CountryLookupService {

    private final RestClient restClient = RestClient.create();

    public String getCountryCode(String ipAddress) {

        if (ipAddress == null || ipAddress.isBlank()) {
            return "UNKNOWN";
        }

        try {
            InetAddress address = InetAddress.getByName(ipAddress);

            if (address.isAnyLocalAddress()
                    || address.isLoopbackAddress()
                    || address.isSiteLocalAddress()
                    || address.isLinkLocalAddress()
                    || address.isMulticastAddress()) {

                return "LOCAL";
            }

            String url = "https://ipapi.co/" + ipAddress + "/country/";

            String countryCode = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);

            if (countryCode == null || countryCode.isBlank()) {
                return "UNKNOWN";
            }

            return countryCode.trim().toUpperCase();

        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}
