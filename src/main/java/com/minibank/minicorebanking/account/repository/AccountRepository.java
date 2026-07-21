package com.minibank.minicorebanking.account.repository;

import com.minibank.minicorebanking.account.dto.AccountResponse;
import com.minibank.minicorebanking.account.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository <Account, Long>{
    Boolean existsByAccountNo(String accountNo);
    Optional<Account> findByAccountNo(String accountNo);
    List<Account> findByCustomerId(Long customerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.accountNo = :accountNo")
    Optional<Account> findByAccountNoWithLock(@Param("accountNo") String accountNo);
}
