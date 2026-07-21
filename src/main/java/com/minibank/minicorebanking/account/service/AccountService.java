package com.minibank.minicorebanking.account.service;

import com.minibank.minicorebanking.account.dto.AccountCreateRequest;
import com.minibank.minicorebanking.account.dto.AccountResponse;
import com.minibank.minicorebanking.account.entity.Account;
import com.minibank.minicorebanking.account.repository.AccountRepository;
import com.minibank.minicorebanking.common.exception.BusinessException;
import com.minibank.minicorebanking.common.exception.ErrorCode;
import com.minibank.minicorebanking.customer.entity.Customer;
import com.minibank.minicorebanking.customer.repository.CustomerRepository;
import com.minibank.minicorebanking.transaction.dto.TransactionResponse;
import com.minibank.minicorebanking.transaction.entity.TransactionHistory;
import com.minibank.minicorebanking.transaction.entity.TransactionType;
import com.minibank.minicorebanking.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    // 계좌 개설
    public AccountResponse createAccount(AccountCreateRequest request){
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

        String accountNo = generateAccountNo();

        Account account = Account.builder()
                .customer(customer)
                .accountNo(accountNo)
                .accountType(request.accountType())
                .build();

        return AccountResponse.from(accountRepository.save(account));
    }

    // 계좌 조회
    @Transactional(readOnly = true)
    public AccountResponse getAccount(String accountNo){
        Account account = accountRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        return AccountResponse.from(account);
    }

    // 고객 계좌 목록 조회
    @Transactional(readOnly = true)
    public List<AccountResponse> getCustomerAccounts(Long customerId){
        if (!customerRepository.existsById(customerId)){
            throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND);
        }

        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        return accounts.stream().map(AccountResponse::from).toList();
    }

    // 계좌 해지
    @Transactional
    public void closeAccount(String accountNo){
        Account account = accountRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        account.close();
    }

    // 입금
    @Transactional
    public TransactionResponse deposit(String accountNo, long amount){
        Account account = accountRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.deposit(amount);

        TransactionHistory transactionHistory = TransactionHistory.builder()
                .accountId(account.getId())
                .transactionType(TransactionType.DEPOSIT)
                .amount(amount)
                .balanceAfter(account.getBalance())
                .build();

        transactionRepository.save(transactionHistory);

        return TransactionResponse.from(transactionHistory);
    }

    // 출금
    @Transactional
    public TransactionResponse withdraw(String accountNo, long amount){
        Account account = accountRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.withdraw(amount);

        TransactionHistory transactionHistory = TransactionHistory.builder()
                .accountId(account.getId())
                .transactionType(TransactionType.WITHDRAW)
                .amount(amount)
                .balanceAfter(account.getBalance())
                .build();

        transactionRepository.save(transactionHistory);

        return TransactionResponse.from(transactionHistory);
    }



    public String generateAccountNo(){
        for(int i = 0; i < 5; i++){
            long randomNumber = ThreadLocalRandom.current().nextLong(0, 100000000);
            String accountNo = "110-" + String.format("%09d", randomNumber);

            if(!accountRepository.existsByAccountNo(accountNo)){
                return accountNo;
            }
        }
        throw new BusinessException(ErrorCode.ACCOUNT_NO_GENERATION_FAILED);
    }
}


