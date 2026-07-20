package com.minibank.minicorebanking.transfer.service;

import com.minibank.minicorebanking.account.entity.Account;
import com.minibank.minicorebanking.account.repository.AccountRepository;
import com.minibank.minicorebanking.common.exception.BusinessException;
import com.minibank.minicorebanking.common.exception.ErrorCode;
import com.minibank.minicorebanking.transaction.entity.TransactionHistory;
import com.minibank.minicorebanking.transaction.entity.TransactionType;
import com.minibank.minicorebanking.transaction.repository.TransactionRepository;
import com.minibank.minicorebanking.transfer.dto.TransferRequest;
import com.minibank.minicorebanking.transfer.dto.TransferResponse;
import com.minibank.minicorebanking.transfer.entity.Transfer;
import com.minibank.minicorebanking.transfer.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransferResponse transfer(TransferRequest request){
        if(request.fromAccountNo().equals(request.toAccountNo())){
            throw new BusinessException(ErrorCode.SAME_ACCOUNT_TRANSFER);
        }

        Account fromAccount = accountRepository.findByAccountNo(request.fromAccountNo())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        Account toAccount = accountRepository.findByAccountNo(request.toAccountNo())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        Transfer transfer = Transfer.builder()
                .idempotencyKey(request.idempotencyKey())
                .fromAccountId(fromAccount.getId())
                .toAccountId(toAccount.getId())
                .amount(request.amount())
                .fee(0L)
                .build();

        transferRepository.save(transfer);

        fromAccount.withdraw(transfer.getAmount());
        toAccount.deposit(transfer.getAmount());

        // 출금 기록
        TransactionHistory outHistory = TransactionHistory.builder()
                .accountId(fromAccount.getId())
                .transactionType(TransactionType.TRANSFER_OUT)
                .amount(request.amount())
                .balanceAfter(fromAccount.getBalance())
                .counterpartyAccountNo(request.toAccountNo())
                .transferId(transfer.getId())
                .build();
        transactionRepository.save(outHistory);

        // 입금 기록
        TransactionHistory inHistory = TransactionHistory.builder()
                .accountId(toAccount.getId())
                .transactionType(TransactionType.TRANSFER_IN)
                .amount(request.amount())
                .balanceAfter(toAccount.getBalance())
                .counterpartyAccountNo(request.toAccountNo())
                .transferId(transfer.getId())
                .build();
        transactionRepository.save(inHistory);

        transfer.complete();

        return TransferResponse.from(transfer, request.fromAccountNo(), request.toAccountNo());
    }
}
