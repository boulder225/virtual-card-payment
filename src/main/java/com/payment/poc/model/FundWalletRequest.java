package com.payment.poc.model;

import java.math.BigDecimal;

public record FundWalletRequest(String userId, BigDecimal amount) {} 