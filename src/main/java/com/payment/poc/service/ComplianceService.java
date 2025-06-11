package com.payment.poc.service;

import org.springframework.stereotype.Service;
import java.util.Set;

/**
 * Compliance service for geo-blocking and regulatory checks
 * Focuses on Vietnam market with extensible rule engine
 */
@Service
public class ComplianceService {
    
    // Allowed countries - primarily Asian markets
    private final Set<String> allowedCountries = Set.of("VN", "KR", "JP", "KZ", "KG");
    
    public boolean isTransactionAllowed(String ipAddress) {
        String countryCode = getCountryFromIp(ipAddress);
        boolean allowed = allowedCountries.contains(countryCode);
        
        if (!allowed) {
            System.out.printf("🚫 Blocked transaction from country: %s (IP: %s)%n", countryCode, ipAddress);
        } else {
            System.out.printf("✅ Transaction allowed from country: %s (IP: %s)%n", countryCode, ipAddress);
        }
        
        return allowed;
    }
    
    public String getCountryFromIp(String ipAddress) {
        // Mock IP-to-country mapping - in production use MaxMind or similar
        if (ipAddress.startsWith("203.113")) return "VN"; // Vietnam IP range
        if (ipAddress.startsWith("125.209")) return "KR"; // South Korea
        if (ipAddress.startsWith("126.")) return "JP";    // Japan
        if (ipAddress.startsWith("91.185")) return "FR";  // France (blocked)
        if (ipAddress.startsWith("185.220")) return "DE"; // Germany (blocked)
        if (ipAddress.startsWith("8.8.")) return "US";    // US (blocked)
        if (ipAddress.startsWith("192.168.")) return "VN"; // Local network, default to VN for testing
        
        return "UNKNOWN"; // Return UNKNOWN for unrecognized IPs
    }
    
    public void logComplianceEvent(String userId, String ipAddress, String action) {
        String countryCode = getCountryFromIp(ipAddress);
        System.out.printf("📋 Compliance Log: User=%s, Country=%s, Action=%s, IP=%s%n", 
                         userId, countryCode, action, ipAddress);
    }
}
