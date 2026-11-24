
package com.onedao.auth.geolocation;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class GeoLocationService {

    public String getClientIp(HttpServletRequest request) {

        return request.getRemoteAddr();
    }
}
