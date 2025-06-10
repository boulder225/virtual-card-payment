package com.payment.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VirtualCardPaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(VirtualCardPaymentApplication.class, args);
    }
} 