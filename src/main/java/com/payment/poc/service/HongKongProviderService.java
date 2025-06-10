package com.payment.poc.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mock Hong Kong card provider integration
 * Simulates the actual card provider API
 */
@Service
public class HongKongProviderService {
    
    public String authorizePayment(String userId, BigDecimal amount, String countryCode) {
        // Simulate API call latency
        try {
            Thread.sleep(200); // 200ms simulated network call
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Mock authorization logic
        boolean authorized = amount.compareTo(new BigDecimal("1000")) <= 0; // Max $1000
        
        if (authorized) {
            String providerId = "HK_" + UUID.randomUUID().toString().substring(0, 8);
            System.out.printf("🏦 HK Provider AUTHORIZED: $%.2f for user %s (ID: %s)%n", 
                             amount, userId, providerId);
            return providerId;
        } else {
            System.out.printf("🏦 HK Provider DECLINED: $%.2f for user %s (amount too high)%n", 
                             amount, userId);
            throw new RuntimeException("Payment declined by Hong Kong provider");
        }
    }
    
    public boolean isSettled(String hkProviderId) {
        // Mock settlement check - 80% chance of being settled
        boolean settled = Math.random() > 0.2;
        
        if (settled) {
            System.out.printf("🏦 HK Provider: Transaction %s is SETTLED%n", hkProviderId);
        } else {
            System.out.printf("🏦 HK Provider: Transaction %s still PENDING%n", hkProviderId);
        }
        
        return settled;
    }
}
