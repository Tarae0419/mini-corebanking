package com.minibank.minicorebanking.account.service;

import com.minibank.minicorebanking.account.dto.AccountCreateRequest;
import com.minibank.minicorebanking.account.dto.AccountResponse;
import com.minibank.minicorebanking.account.entity.Account;
import com.minibank.minicorebanking.account.repository.AccountRepository;
import com.minibank.minicorebanking.common.exception.BusinessException;
import com.minibank.minicorebanking.common.exception.ErrorCode;
import com.minibank.minicorebanking.customer.entity.Customer;
import com.minibank.minicorebanking.customer.repository.CustomerRepository;
import com.minibank.minicorebanking.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

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


