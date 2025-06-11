package com.payment.poc.controller;

import com.payment.poc.model.PaymentRequest;
import com.payment.poc.model.Transaction;
import com.payment.poc.model.TransactionStatus;
import com.payment.poc.model.FundWalletRequest;
import com.payment.poc.service.*;
import com.payment.poc.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Main payment controller demonstrating virtual card payment flow
 * Handles payment processing and transaction management
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private ComplianceService complianceService;
    
    @Autowired
    private HongKongProviderService hkProviderService;
    
    @Autowired
    private TransactionRepository transactionRepo;
    
    /**
     * Process payment request - core flow demonstration
     * Shows Vietnam user success path and EU geo-blocking
     */
    @PostMapping
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest request) {
        try {
            System.out.printf("💳 Processing payment: $%.2f for user %s from IP %s%n", 
                             request.amount(), request.userId(), request.ipAddress());
            
            // 1. Compliance check - geo-blocking for non-Asian countries
            if (!complianceService.isTransactionAllowed(request.ipAddress())) {
                complianceService.logComplianceEvent(request.userId(), request.ipAddress(), "BLOCKED");
                return ResponseEntity.badRequest()
                    .body("Transaction not allowed from your location");
            }
            
            // 2. Wallet balance check
            if (!walletService.hasSufficientFunds(request.userId(), request.amount())) {
                return ResponseEntity.badRequest()
                    .body("Insufficient funds");
            }
            
            // 3. Lock funds in custodial wallet
            walletService.lockFunds(request.userId(), request.amount());
            
            // 4. Create transaction record
            Transaction transaction = new Transaction(
                request.userId(), 
                request.amount(), 
                complianceService.getCountryFromIp(request.ipAddress()),
                request.ipAddress()
            );
            
            // 5. Authorize with Hong Kong provider
            try {
                String hkProviderId = hkProviderService.authorizePayment(
                    request.userId(), 
                    request.amount(), 
                    transaction.getCountryCode()
                );
                
                transaction.setHkProviderId(hkProviderId);
                transaction.setStatus(TransactionStatus.PENDING);
                transaction = transactionRepo.save(transaction);
                
                complianceService.logComplianceEvent(request.userId(), request.ipAddress(), "AUTHORIZED");
                
                return ResponseEntity.ok(transaction);
                
            } catch (Exception e) {
                // Authorization failed - release locked funds
                walletService.releaseFunds(request.userId(), request.amount());
                transaction.setStatus(TransactionStatus.FAILED);
                transactionRepo.save(transaction);
                
                return ResponseEntity.badRequest()
                    .body("Payment authorization failed: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.printf("❌ Payment processing error: %s%n", e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Payment processing failed: " + e.getMessage());
        }
    }
    
    /**
     * Get transaction status - useful for demo
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long transactionId) {
        return transactionRepo.findById(transactionId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * List all transactions - demo purposes
     */
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionRepo.findAll();
    }
    
    /**
     * Get user wallet balance - demo purposes
     */
    @GetMapping("/balance/{userId}")
    public ResponseEntity<String> getUserBalance(@PathVariable String userId) {
        try {
            var balance = walletService.getBalance(userId);
            return ResponseEntity.ok(String.format("User %s balance: $%.2f USDC", userId, balance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("User not found");
        }
    }
    
    /**
     * Fund a user's wallet
     */
    @PostMapping("/fund")
    public ResponseEntity<String> fundWallet(@RequestBody FundWalletRequest request) {
        try {
            walletService.addFunds(request.userId(), request.amount());
            return ResponseEntity.ok(String.format("Added $%.2f USDC to user %s", request.amount(), request.userId()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to fund wallet: " + e.getMessage());
        }
    }
}
