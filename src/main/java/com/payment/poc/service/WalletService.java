package com.payment.poc.service;

import java.math.BigDecimal;

/**
 * Unified wallet interface for custodial and future non-custodial wallets
 * Demonstrates abstraction pattern for wallet migration strategy
 */
public interface WalletService {
    void lockFunds(String userId, BigDecimal amount);
    void releaseFunds(String userId, BigDecimal amount);
    BigDecimal getBalance(String userId);
    boolean hasSufficientFunds(String userId, BigDecimal amount);
    void addFunds(String userId, BigDecimal amount);
}
