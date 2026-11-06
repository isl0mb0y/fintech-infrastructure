--liquibase formatted sql

--changeset isl0mb0y:001-customers
CREATE TABLE customers (
    id               UUID PRIMARY KEY,
    type             VARCHAR(20)  NOT NULL,
    full_name        VARCHAR(100) NOT NULL,
    phone            VARCHAR(13)  NOT NULL UNIQUE,
    document_number  VARCHAR(20)  NOT NULL UNIQUE,
    created_at       TIMESTAMPTZ  NOT NULL
);
--rollback DROP TABLE customers;

--changeset isl0mb0y:001-accounts
CREATE TABLE accounts (
    id           UUID PRIMARY KEY,
    number       VARCHAR(20)  NOT NULL UNIQUE,
    code         VARCHAR(30)  UNIQUE,
    customer_id  UUID,
    type         VARCHAR(10)  NOT NULL,
    currency     VARCHAR(3)   NOT NULL,
    status       VARCHAR(10)  NOT NULL,
    balance      BIGINT       NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL,
    CONSTRAINT accounts_customer_fk FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT accounts_owner_chk CHECK (
        (type = 'CUSTOMER' AND customer_id IS NOT NULL) OR
        (type = 'INTERNAL' AND customer_id IS NULL AND code IS NOT NULL)
    )
);
CREATE INDEX accounts_customer_id_idx ON accounts (customer_id);
--rollback DROP TABLE accounts;

--changeset isl0mb0y:001-journal-entries
CREATE TABLE journal_entries (
    id           UUID PRIMARY KEY,
    type         VARCHAR(20)  NOT NULL,
    reference    VARCHAR(64)  NOT NULL UNIQUE,
    description  VARCHAR(255),
    created_at   TIMESTAMPTZ  NOT NULL
);
--rollback DROP TABLE journal_entries;

--changeset isl0mb0y:001-postings
CREATE TABLE postings (
    id          UUID PRIMARY KEY,
    entry_id    UUID         NOT NULL,
    account_id  UUID         NOT NULL,
    amount      BIGINT       NOT NULL,
    value_date  DATE         NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL,
    CONSTRAINT postings_entry_fk   FOREIGN KEY (entry_id)   REFERENCES journal_entries (id),
    CONSTRAINT postings_account_fk FOREIGN KEY (account_id) REFERENCES accounts (id),
    CONSTRAINT postings_amount_chk CHECK (amount <> 0)
);
CREATE INDEX postings_entry_id_idx ON postings (entry_id);
CREATE INDEX postings_account_created_idx ON postings (account_id, created_at);
--rollback DROP TABLE postings;

--changeset isl0mb0y:001-account-number-seq
CREATE SEQUENCE account_number_seq;
--rollback DROP SEQUENCE account_number_seq;

--changeset isl0mb0y:001-internal-accounts
INSERT INTO accounts (id, number, code, customer_id, type, currency, status, balance, created_at) VALUES
    (gen_random_uuid(), '10101000000000000001', 'CASH',            NULL, 'INTERNAL', 'UZS', 'OPEN', 0, now()),
    (gen_random_uuid(), '17301000000000000001', 'CARD_SETTLEMENT', NULL, 'INTERNAL', 'UZS', 'OPEN', 0, now());
--rollback DELETE FROM accounts WHERE code IN ('CASH', 'CARD_SETTLEMENT');
