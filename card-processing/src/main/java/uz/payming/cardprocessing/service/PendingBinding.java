package uz.payming.cardprocessing.service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class PendingBinding {
    private final UUID tempId;
    private final UUID cardToken;
    private final String otp;
    private final Instant expiresAt;
    private final AtomicInteger failedAttempts = new AtomicInteger();

    public PendingBinding(UUID tempId, UUID cardToken, String otp, Instant expiresAt) {
        this.tempId = tempId;
        this.cardToken = cardToken;
        this.otp = otp;
        this.expiresAt = expiresAt;
    }

    /** Increment and returns the new count. */
    public int registerFailedAttempt() {
        return failedAttempts.incrementAndGet();
    }

    public UUID tempId() {return tempId;}
    public UUID cardToken() {return cardToken;}
    public String otp() {return otp;}
    public Instant expiresAt() {return expiresAt;}



}
