package com.payment.poc.model;

import java.math.BigDecimal;

public record PaymentRequest(
    String userId,
    BigDecimal amount,
    String ipAddress
) {}
