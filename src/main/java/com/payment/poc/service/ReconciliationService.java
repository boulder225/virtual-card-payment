package com.payment.poc.service;

import com.payment.poc.model.Transaction;
import com.payment.poc.model.TransactionStatus;
import com.payment.poc.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Automated reconciliation service that checks pending transactions
 * and updates their status based on provider response
 */
@Service
public class ReconciliationService {
    
    @Autowired
    private TransactionRepository transactionRepo;
    
    @Autowired
    private WalletService walletService;
    
    /**
     * Scheduled reconciliation job - runs every 30 seconds
     * Checks all pending transactions and updates their status
     */
    @Scheduled(fixedRate = 30000)
    public void reconcilePendingTransactions() {
        System.out.println("🔄 Running reconciliation cycle...");
        
        List<Transaction> pendingTransactions = transactionRepo.findByStatus(TransactionStatus.PENDING);
        System.out.printf("Found %d pending transactions%n", pendingTransactions.size());
        
        for (Transaction tx : pendingTransactions) {
            reconcileTransaction(tx);
        }
    }
    
    /**
     * Process a single transaction for reconciliation
     * Updates status based on provider response
     */
    private void reconcileTransaction(Transaction tx) {
        try {
            System.out.printf("Reconciling transaction %d for user %s%n", 
                            tx.getId(), tx.getUserId());
            
            // Simulate provider response
            boolean isSettled = Math.random() > 0.1; // 90% success rate
            
            if (isSettled) {
                tx.setStatus(TransactionStatus.SETTLED);
                transactionRepo.save(tx);
                System.out.println("✅ Transaction settled successfully");
            } else {
                tx.setStatus(TransactionStatus.FAILED);
                transactionRepo.save(tx);
                walletService.releaseFunds(tx.getUserId(), tx.getAmount());
                System.out.println("❌ Transaction failed - funds released");
            }
            
        } catch (Exception e) {
            System.err.printf("Error reconciling transaction %d: %s%n", 
                            tx.getId(), e.getMessage());
        }
    }
}
