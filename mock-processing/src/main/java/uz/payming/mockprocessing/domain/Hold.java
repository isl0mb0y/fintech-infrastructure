package uz.payming.mockprocessing.domain;

import java.time.Instant;
import java.util.UUID;

public class Hold {
    private final UUID holdId;
    private final UUID requestId;
    private final String processingTxId;
    private final UUID cardToken;
    private final long amount;
    private final Instant createdAt;
    private final Instant expiresAt;
    private HoldStatus status;
    private Instant settledAt; // set when CONFIRMED, used by the registry

    public Hold(UUID requestId,
                String processingTxId,
                UUID cardToken,
                long amount,
                Instant createdAt,
                Instant expiresAt) {
        this.holdId = UUID.randomUUID();
        this.requestId = requestId;
        this.processingTxId = processingTxId;
        this.cardToken = cardToken;
        this.amount = amount;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.status = HoldStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == HoldStatus.ACTIVE;
    }

    public UUID getHoldId() {
        return holdId;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public String getProcessingTxId() {
        return processingTxId;
    }

    public UUID getCardToken() {
        return cardToken;
    }

    public long getAmount() {
        return amount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public HoldStatus getStatus() {
        return status;
    }

    public Instant getSettledAt() {
        return settledAt;
    }

    public void setSettledAt(Instant settledAt) {
        this.settledAt = settledAt;
    }

    public void setStatus(HoldStatus status) {
        this.status = status;
    }
}
