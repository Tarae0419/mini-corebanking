package com.minibank.minicorebanking.transaction.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private Long amount;

    @Column(name = "balance_after", nullable = false)
    private Long balanceAfter;

    @Column(name = "counterparty_account_no")
    private String counterpartyAccountNo;

    @Column(name = "transfer_id")
    private Long transferId;

    private String memo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
