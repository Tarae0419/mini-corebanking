package com.minibank.minicorebanking.account.repository;

import com.minibank.minicorebanking.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository <Account, Long>{
    Boolean existsByAccountNo(String accountNo);
    Optional<Account> findByAccountNo(String accountNo);
}
