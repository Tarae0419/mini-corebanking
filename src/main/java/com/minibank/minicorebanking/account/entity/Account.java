package com.minibank.minicorebanking.account.entity;

import com.minibank.minicorebanking.common.exception.BusinessException;
import com.minibank.minicorebanking.common.exception.ErrorCode;
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

    // @Version
    private Long version = 0L;

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

    public void close(){
        if(this.status == AccountStatus.CLOSED){
            throw new BusinessException(ErrorCode.ALREADY_CLOSED);
        }
        if(this.balance > 0){
            throw new BusinessException(ErrorCode.BALANCE_REMAINING);
        }
        this.status = AccountStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
    }

    public void deposit(long amount){
        if(this.status != AccountStatus.ACTIVE){
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }

        this.balance += amount;
        this.lastTransactionAt = LocalDateTime.now();
    }

    public void withdraw(long amount){
        if(this.status != AccountStatus.ACTIVE){
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }
        if(this.balance < amount){
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        this.balance -= amount;
        this.lastTransactionAt = LocalDateTime.now();
    }
}

