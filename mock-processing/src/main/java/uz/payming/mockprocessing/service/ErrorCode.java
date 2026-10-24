package uz.payming.mockprocessing.service;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    CARD_NOT_FOUND(HttpStatus.NOT_FOUND, false),
    CARD_BLOCKED(HttpStatus.UNPROCESSABLE_ENTITY, false),
    CARD_EXPIRED(HttpStatus.UNPROCESSABLE_ENTITY, false),
    PHONE_MISMATCH(HttpStatus.UNPROCESSABLE_ENTITY, false),
    TEMP_ID_NOT_FOUND(HttpStatus.NOT_FOUND, false),
    INVALID_OTP(HttpStatus.UNPROCESSABLE_ENTITY, true),
    OTP_EXPIRED(HttpStatus.UNPROCESSABLE_ENTITY, false),
    TOO_MANY_ATTEMPTS(HttpStatus.UNPROCESSABLE_ENTITY, false),
    INSUFFICIENT_FUNDS(HttpStatus.UNPROCESSABLE_ENTITY, false),
    LIMIT_EXCEEDED(HttpStatus.UNPROCESSABLE_ENTITY, false),
    REQUEST_ID_CONFLICT(HttpStatus.CONFLICT, false),
    HOLD_NOT_FOUND(HttpStatus.NOT_FOUND, false),
    HOLD_EXPIRED(HttpStatus.UNPROCESSABLE_ENTITY, false),
    HOLD_REVERSED(HttpStatus.UNPROCESSABLE_ENTITY, false),
    HOLD_CONFIRMED(HttpStatus.UNPROCESSABLE_ENTITY, false),
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, true);

    private final HttpStatus status;
    private final boolean retryable;

    ErrorCode(HttpStatus status, boolean retryable) {
        this.status = status;
        this.retryable = retryable;
    }

    public HttpStatus status() {return  status;}
    public boolean retryable() {return retryable;}
}
