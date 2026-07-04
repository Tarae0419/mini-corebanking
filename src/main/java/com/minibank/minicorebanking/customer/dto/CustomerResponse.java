package com.minibank.minicorebanking.customer.dto;

import com.minibank.minicorebanking.customer.entity.Customer;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerResponse(
        Long id,
        String name,
        LocalDate birthDate,
        String phone,
        String email,
        LocalDateTime createdAt
){
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(), customer.getName(), customer.getBirthDate(),
                customer.getPhone(), customer.getEmail(), customer.getCreatedAt()
        );
    }
}
