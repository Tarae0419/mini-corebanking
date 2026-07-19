package com.minibank.minicorebanking.account.entity;

import com.minibank.minicorebanking.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_no", nullable = false, unique = true)
    private String accountNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(nullable = false)
    private Long balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(name = "daily_transfer_limit", nullable = false)
    private Long dailyTransferLimit;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "last_transaction_at")
    private LocalDateTime lastTransactionAt;

    @Version
    private Long version;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Builder
    private Account (Customer customer, String accountNo, AccountType accountType, Long balance, AccountStatus status, Long dailyTransferLimit, BigDecimal interestRate) {
        this.customer = customer;
        this.accountNo = accountNo;
        this.accountType = accountType;
        this.balance = 0L;
        this.status = AccountStatus.ACTIVE;;
        this.dailyTransferLimit = 5000000L;
        this.interestRate = new BigDecimal("0.0100");;
        this.createdAt = LocalDateTime.now();
    }
}

