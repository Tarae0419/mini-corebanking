package com.minibank.minicorebanking.transfer.repository;

import com.minibank.minicorebanking.transfer.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
