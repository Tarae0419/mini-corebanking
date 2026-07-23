package com.minibank.minicorebanking.transfer;

import com.minibank.minicorebanking.account.dto.AccountCreateRequest;
import com.minibank.minicorebanking.account.dto.AccountResponse;
import com.minibank.minicorebanking.account.entity.AccountType;
import com.minibank.minicorebanking.account.service.AccountService;
import com.minibank.minicorebanking.customer.dto.CustomerCreateRequest;
import com.minibank.minicorebanking.customer.dto.CustomerResponse;
import com.minibank.minicorebanking.customer.service.CustomerService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
public class DummyDataGenerator {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    CustomerService customerService;
    @Autowired
    AccountService accountService;

    @Test
    // @Disabled("데이터 생성용 - 필요할 때만 수동 실행")
    void generateTransactionHistory() {
        CustomerResponse customer = customerService.createCustomer(
                new CustomerCreateRequest("동시성테스트", LocalDate.of(2000, 1, 1),
                        "010-" + (int)(Math.random() * 9000 + 1000) + "-0000", null)
        );

        AccountResponse accountA = accountService.createAccount(new AccountCreateRequest(customer.id(), AccountType.CHECKING));

        long accountId = accountA.id();;
        System.out.println("대상 계좌번호 (2단계 측정에 사용): " + accountA.accountNo());

        int total = 1_000_000;
        int batchSize = 1_000;

        long start = System.currentTimeMillis();

        String sql = "INSERT INTO transaction_history " +
                "(account_id, transaction_type, amount, balance_after, created_at) " +
                "VALUES (?, ?, ?, ?, ?)";

        for (int batch = 0; batch < total / batchSize; batch++) {
            int base = batch * batchSize;
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, accountId);
                    ps.setString(2, (base + i) % 2 == 0 ? "DEPOSIT" : "WITHDRAW");
                    ps.setLong(3, ThreadLocalRandom.current().nextLong(1_000, 500_000));
                    ps.setLong(4, ThreadLocalRandom.current().nextLong(0, 10_000_000));
                    ps.setTimestamp(5, Timestamp.valueOf(
                            LocalDateTime.now().minusMinutes((long)(base + i) / 2)));
                }
                @Override
                public int getBatchSize() { return batchSize; }
            });
            if (batch % 100 == 0) System.out.println("진행: " + base + "건");
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("========================================");
        System.out.println("100만 건 생성 완료: " + (elapsed / 1000.0) + "초");
        System.out.println("계좌 ID: " + accountId + " / 계좌번호: " + accountA.accountNo());
        System.out.println("========================================");
    }
}
