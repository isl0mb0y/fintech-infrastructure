--liquibase formatted sql

--changeset isl0mb0y:001-banks
CREATE TABLE banks (
    id                  VARCHAR(20)  PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    bic                 VARCHAR(10)  NOT NULL UNIQUE,
    settlement_account  VARCHAR(20)  NOT NULL
);
--rollback DROP TABLE banks;

--changeset isl0mb0y:001-cards
CREATE TABLE cards
(
    token          UUID PRIMARY KEY,
    bank_id        VARCHAR(20)  NOT NULL REFERENCES banks (id),
    account_number VARCHAR(20)  NOT NULL,
    pan            VARCHAR(16)  NOT NULL UNIQUE,
    phone          VARCHAR(13)  NOT NULL,
    owner          VARCHAR(100) NOT NULL,
    network        VARCHAR(10)  NOT NULL,
    expiry         VARCHAR(7)   NOT NULL,
    balance        BIGINT       NOT NULL,
    available      BIGINT       NOT NULL,
    status         varchar(10)  NOT NULL
);
CREATE INDEX cards_bank_id_idx ON cards (bank_id);
CREATE INDEX cards_account_number_idx ON cards (account_number);
--rollback DROP TABLE cards;

--changeset isl0mb0y:001-holds
CREATE TABLE holds
(
    hold_id          UUID PRIMARY KEY,
    request_id       UUID        NOT NULL UNIQUE,
    processing_tx_id VARCHAR(30) NOT NULL UNIQUE,
    card_token       UUID        NOT NULL REFERENCES cards (token),
    amount           BIGINT      NOT NULL,
    status           VARCHAR(10) NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL,
    expires_at       TIMESTAMPTZ  NOT NULL,
    settled_at       TIMESTAMPTZ
);
CREATE INDEX holds_status_expires_idx ON holds(status, expires_at);
CREATE INDEX holds_settled_at_idx ON holds (settled_at);
--rollback DROP TABLE holds;

--changeset isl0mb0y:001-credits
CREATE TABLE credits
(
    credit_id UUID PRIMARY KEY,
    request_id UUID NOT NULL UNIQUE,
    processing_tx_id VARCHAR(30) NOT NULL UNIQUE,
    card_token UUID NOT NULL REFERENCES cards(token),
    amount BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX credits_created_at_idx ON credits (created_at);
--rollback DROP TABLE credits;

--changeset isl0mb0y:001-tx-seq
CREATE SEQUENCE processing_tx_seq;
--rollback DROP SEQUENCE processing_tx_seq;
