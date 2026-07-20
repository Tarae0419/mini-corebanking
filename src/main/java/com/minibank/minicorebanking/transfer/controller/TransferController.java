package com.minibank.minicorebanking.transfer.controller;

import com.minibank.minicorebanking.transfer.dto.TransferRequest;
import com.minibank.minicorebanking.transfer.dto.TransferResponse;
import com.minibank.minicorebanking.transfer.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(transferService.transfer(request));
    }
}
