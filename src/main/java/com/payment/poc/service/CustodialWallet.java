package com.payment.poc.service;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock implementation of the current custodial wallet system
 */
@Service
@Primary
public class CustodialWallet implements WalletService {
    
    // Mock user balances - in production this would be database/external service
    private final Map<String, BigDecimal> userBalances = new ConcurrentHashMap<>();
    private final Map<String, BigDecimal> lockedFunds = new ConcurrentHashMap<>();
    
    public CustodialWallet() {
        // Initialize some test users
        userBalances.put("vietnam_user_1", new BigDecimal("1000.00"));
        userBalances.put("vietnam_user_2", new BigDecimal("500.00"));
        userBalances.put("france_user_1", new BigDecimal("750.00"));
    }

    @Override
    public void lockFunds(String userId, BigDecimal amount) {
        BigDecimal currentBalance = getBalance(userId);
        if (currentBalance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        
        BigDecimal currentLocked = lockedFunds.getOrDefault(userId, BigDecimal.ZERO);
        lockedFunds.put(userId, currentLocked.add(amount));
        
        System.out.printf("🔒 Locked $%.2f USDC for user: %s%n", amount, userId);
    }

    @Override
    public void releaseFunds(String userId, BigDecimal amount) {
        BigDecimal currentLocked = lockedFunds.getOrDefault(userId, BigDecimal.ZERO);
        if (currentLocked.compareTo(amount) < 0) {
            throw new RuntimeException("Cannot release more funds than locked");
        }
        
        lockedFunds.put(userId, currentLocked.subtract(amount));
        System.out.printf("🔓 Released $%.2f USDC for user: %s%n", amount, userId);
    }

    @Override
    public BigDecimal getBalance(String userId) {
        BigDecimal total = userBalances.getOrDefault(userId, BigDecimal.ZERO);
        BigDecimal locked = lockedFunds.getOrDefault(userId, BigDecimal.ZERO);
        return total.subtract(locked);
    }

    @Override
    public boolean hasSufficientFunds(String userId, BigDecimal amount) {
        return getBalance(userId).compareTo(amount) >= 0;
    }
}
