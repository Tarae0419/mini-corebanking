package com.minibank.minicorebanking.customer.service;

import com.minibank.minicorebanking.common.exception.BusinessException;
import com.minibank.minicorebanking.common.exception.ErrorCode;
import com.minibank.minicorebanking.customer.dto.CustomerCreateRequest;
import com.minibank.minicorebanking.customer.dto.CustomerResponse;
import com.minibank.minicorebanking.customer.entity.Customer;
import com.minibank.minicorebanking.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        if (customerRepository.existsByPhone(request.phone())) {
            throw new BusinessException(ErrorCode.DUPLICATE_PHONE);
        }

        Customer customer = Customer.builder()
                .name(request.name())
                .birthDate(request.birthDate())
                .phone(request.phone())
                .email(request.email())
                .build();

        Customer saved = customerRepository.save(customer);
        return CustomerResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));
        return CustomerResponse.from(customer);
    }
}