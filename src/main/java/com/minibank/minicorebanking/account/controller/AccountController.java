package com.minibank.minicorebanking.account.controller;

import com.minibank.minicorebanking.account.dto.AccountCreateRequest;
import com.minibank.minicorebanking.account.dto.AccountResponse;
import com.minibank.minicorebanking.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    // 계좌 개설
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountCreateRequest request){
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 계좌 조회
    @GetMapping("/{accountNo}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNo){
        return ResponseEntity.ok(accountService.getAccount(accountNo));
    }

    // 고객 계좌 목록 조회
    @GetMapping(params = "customerId")
    public ResponseEntity<List<AccountResponse>> getCustomerAccounts(@RequestParam Long customerId){
        return ResponseEntity.ok(accountService.getCustomerAccounts(customerId));
    }

    // 계좌 해지
    @DeleteMapping("/{accountNo}")
    public ResponseEntity<Void> closeAccount(@PathVariable String accountNo){
        accountService.closeAccount(accountNo);
        return ResponseEntity.noContent().build();
    }
}
