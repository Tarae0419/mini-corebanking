package com.minibank.minicorebanking.transfer;

import com.minibank.minicorebanking.account.dto.AccountCreateRequest;
import com.minibank.minicorebanking.account.dto.AccountResponse;
import com.minibank.minicorebanking.account.entity.Account;
import com.minibank.minicorebanking.account.entity.AccountType;
import com.minibank.minicorebanking.account.repository.AccountRepository;
import com.minibank.minicorebanking.account.service.AccountService;
import com.minibank.minicorebanking.customer.dto.CustomerCreateRequest;
import com.minibank.minicorebanking.customer.dto.CustomerResponse;
import com.minibank.minicorebanking.customer.repository.CustomerRepository;
import com.minibank.minicorebanking.customer.service.CustomerService;
import com.minibank.minicorebanking.transfer.dto.TransferRequest;
import com.minibank.minicorebanking.transfer.repository.TransferRepository;
import com.minibank.minicorebanking.transfer.service.TransferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class TransferConcurrencyTest {

    @Autowired
    TransferService transferService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransferRepository transferRepository;

    @Test
    @DisplayName("동시에 100건 이체 시 잔액 정합성 검증")
    void concurrentTransfer() throws InterruptedException{
        CustomerResponse customer = customerService.createCustomer(
                new CustomerCreateRequest("동시성테스트", LocalDate.of(2000, 1, 1),
                        "010-" + (int)(Math.random() * 9000 + 1000) + "-0000", null)
        );

        AccountResponse accountA = accountService.createAccount(new AccountCreateRequest(customer.id(), AccountType.CHECKING));
        AccountResponse accountB = accountService.createAccount(new AccountCreateRequest(customer.id(), AccountType.CHECKING));

        String fromNo = accountA.accountNo();
        String toNo = accountB.accountNo();

        accountService.deposit(fromNo, 1_000_000L);

        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    transferService.transfer(new TransferRequest(
                            fromNo, toNo, 10_000L, UUID.randomUUID().toString()));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("실패 원인: " + e.getClass().getSimpleName() + " / " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Account fromAccount = accountRepository.findByAccountNo(fromNo).orElseThrow();
        Account toAccount = accountRepository.findByAccountNo(toNo).orElseThrow();

        long expectedFromBalance = 1_000_000L - (successCount.get() * 10_000L);
        long expectedToBalance = successCount.get() * 10_000L;
        long lostAmount = fromAccount.getBalance() - expectedFromBalance;

        System.out.println("========================================");
        System.out.println("     동시 이체 정합성 실험 결과");
        System.out.println("========================================");
        System.out.println("실험 조건   : 100 threads, A→B 각 10,000원 이체, 비관적 락");
        System.out.println("성공 응답   : " + successCount.get() + "건 (이동했어야 할 금액: " + expectedToBalance + "원)");
        System.out.println("실패 응답   : " + failCount.get() + "건");
        System.out.println("----------------------------------------");
        System.out.println("A 계좌 기대 : " + expectedFromBalance + "원 / 실제: " + fromAccount.getBalance() + "원");
        System.out.println("B 계좌 기대 : " + expectedToBalance + "원 / 실제: " + toAccount.getBalance() + "원");
        System.out.println("----------------------------------------");
        System.out.println("갱신 유실액 : " + lostAmount + "원 (" + (lostAmount / 10_000L) + "건 상당)");
        System.out.println("========================================");
    }

    @Test
    @DisplayName("동일 멱등성 키로 동시 100건 요청 시 실제 이체는 1건만 실행된다")
    void duplicateIdempotencyKey() throws InterruptedException{
        CustomerResponse customer = customerService.createCustomer(
                new CustomerCreateRequest("동시성테스트", LocalDate.of(2000, 1, 1),
                        "010-" + (int)(Math.random() * 9000 + 1000) + "-0000", null)
        );

        AccountResponse accountA = accountService.createAccount(new AccountCreateRequest(customer.id(), AccountType.CHECKING));
        AccountResponse accountB = accountService.createAccount(new AccountCreateRequest(customer.id(), AccountType.CHECKING));

        String fromNo = accountA.accountNo();
        String toNo = accountB.accountNo();

        accountService.deposit(fromNo, 1_000_000L);

        String fixedKey = UUID.randomUUID().toString();
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    transferService.transfer(new TransferRequest(
                            fromNo, toNo, 10_000L, fixedKey));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("실패 원인: " + e.getClass().getSimpleName() + " / " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Account fromAccount = accountRepository.findByAccountNo(fromNo).orElseThrow();
        Account toAccount = accountRepository.findByAccountNo(toNo).orElseThrow();

        System.out.println("========================================");
        System.out.println("     멱등성 동시 중복 요청 실험 결과");
        System.out.println("========================================");
        System.out.println("실험 조건   : 동일 키로 동시 100건 이체 요청");
        System.out.println("성공 응답   : " + successCount.get() + "건 / 실패 응답: " + failCount.get() + "건");
        System.out.println("A 잔액      : " + fromAccount.getBalance() + "원 (기대: 990000원)");
        System.out.println("B 잔액      : " + toAccount.getBalance() + "원 (기대: 10000원)");
        System.out.println("실제 이체   : " + (1_000_000L - fromAccount.getBalance()) / 10_000L + "건 (기대: 1건)");
        System.out.println("========================================");

        assertThat(fromAccount.getBalance()).isEqualTo(990_000L);
        assertThat(toAccount.getBalance()).isEqualTo(10_000L);
    }
}
