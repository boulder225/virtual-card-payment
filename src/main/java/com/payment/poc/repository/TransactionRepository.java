package com.payment.poc.repository;

import com.payment.poc.model.Transaction;
import com.payment.poc.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByStatus(TransactionStatus status);
    List<Transaction> findByUserId(String userId);
    List<Transaction> findByCountryCode(String countryCode);
}
