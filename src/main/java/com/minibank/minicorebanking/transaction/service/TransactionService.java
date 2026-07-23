package com.minibank.minicorebanking.transaction.service;

import com.minibank.minicorebanking.account.entity.Account;
import com.minibank.minicorebanking.account.repository.AccountRepository;
import com.minibank.minicorebanking.common.exception.BusinessException;
import com.minibank.minicorebanking.common.exception.ErrorCode;
import com.minibank.minicorebanking.transaction.dto.TransactionResponse;
import com.minibank.minicorebanking.transaction.entity.TransactionHistory;
import com.minibank.minicorebanking.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByOffset(String accountNo, int page, int size){
        Account account = accountRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<TransactionHistory> result = transactionRepository.findByAccountId(account.getId(), pageable);

        return result.getContent().stream().map(TransactionResponse::from).toList();
    }
}
