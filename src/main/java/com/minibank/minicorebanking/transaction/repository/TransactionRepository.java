package com.minibank.minicorebanking.transaction.repository;

import com.minibank.minicorebanking.transaction.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionHistory, Long> {
    List<TransactionHistory> findTop20ByAccountIdOrderByCreatedAtDesc(Long accountId);
}
