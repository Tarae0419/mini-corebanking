-- 고갹 테이블
CREATE TABLE customer (
                          id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name            VARCHAR(50)  NOT NULL,
                          birth_date      DATE         NOT NULL,
                          phone           VARCHAR(20)  NOT NULL,
                          email           VARCHAR(100),
                          created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          UNIQUE KEY uk_customer_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 계좌 테이블
CREATE TABLE account (
                         id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
                         account_no           VARCHAR(20)  NOT NULL,
                         customer_id          BIGINT       NOT NULL,
                         account_type         VARCHAR(20)  NOT NULL DEFAULT 'CHECKING',
                         balance              BIGINT       NOT NULL DEFAULT 0,
                         status               VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
                         daily_transfer_limit BIGINT       NOT NULL DEFAULT 5000000,
                         interest_rate        DECIMAL(5,4) NOT NULL DEFAULT 0.0100,
                         last_transaction_at  DATETIME,
                         version              BIGINT       NOT NULL DEFAULT 0,
                         created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         closed_at            DATETIME,
                         UNIQUE KEY uk_account_no (account_no),
                         KEY idx_account_customer (customer_id),
                         KEY idx_account_status_last_tx (status, last_transaction_at),
                         CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
                         CONSTRAINT chk_balance_non_negative CHECK (balance >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 거래내역 테이블
CREATE TABLE transaction_history (
                                     id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     account_id              BIGINT      NOT NULL,
                                     transaction_type        VARCHAR(20) NOT NULL,
                                     amount                  BIGINT      NOT NULL,
                                     balance_after           BIGINT      NOT NULL,
                                     counterparty_account_no VARCHAR(20),
                                     transfer_id             BIGINT,
                                     memo                    VARCHAR(100),
                                     created_at              DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     KEY idx_tx_account_created (account_id, created_at DESC, id DESC),
                                     KEY idx_tx_created (created_at),
                                     CONSTRAINT fk_tx_account FOREIGN KEY (account_id) REFERENCES account(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 이체 테이블
CREATE TABLE transfer (
                          id                BIGINT AUTO_INCREMENT PRIMARY KEY,
                          idempotency_key   VARCHAR(64) NOT NULL,
                          from_account_id   BIGINT      NOT NULL,
                          to_account_id     BIGINT      NOT NULL,
                          amount            BIGINT      NOT NULL,
                          fee               BIGINT      NOT NULL DEFAULT 0,
                          status            VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                          failure_reason    VARCHAR(200),
                          created_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          completed_at      DATETIME,
                          UNIQUE KEY uk_transfer_idempotency (idempotency_key),
                          KEY idx_transfer_from (from_account_id, created_at),
                          KEY idx_transfer_status (status),
                          CONSTRAINT fk_transfer_from FOREIGN KEY (from_account_id) REFERENCES account(id),
                          CONSTRAINT fk_transfer_to   FOREIGN KEY (to_account_id)   REFERENCES account(id),
                          CONSTRAINT chk_transfer_amount_positive CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 마감 테이블
CREATE TABLE daily_settlement (
                                  id                BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  settlement_date   DATE     NOT NULL,
                                  account_id        BIGINT   NOT NULL,
                                  total_deposit     BIGINT   NOT NULL DEFAULT 0,
                                  total_withdraw    BIGINT   NOT NULL DEFAULT 0,
                                  transaction_count INT      NOT NULL DEFAULT 0,
                                  closing_balance   BIGINT   NOT NULL,
                                  created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  UNIQUE KEY uk_settlement_date_account (settlement_date, account_id),
                                  CONSTRAINT fk_settlement_account FOREIGN KEY (account_id) REFERENCES account(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 이자 지급 이력 테이블
CREATE TABLE interest_history (
                                  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  account_id      BIGINT       NOT NULL,
                                  interest_date   DATE         NOT NULL,
                                  base_balance    BIGINT       NOT NULL,
                                  interest_rate   DECIMAL(5,4) NOT NULL,
                                  interest_amount BIGINT       NOT NULL,
                                  created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  UNIQUE KEY uk_interest_account_date (account_id, interest_date),
                                  CONSTRAINT fk_interest_account FOREIGN KEY (account_id) REFERENCES account(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;