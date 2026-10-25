package uz.payming.cardprocessing.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "holds")
public class Hold {
    @Id
    @Column(name = "hold_id")
    private UUID holdId;

    @Column(name = "request_id", nullable = false, unique = true)
    private UUID requestId;

    @Column(name = "processing_tx_id", nullable = false, unique = true)
    private String processingTxId;

    @Column(name = "card_token", nullable = false)
    private UUID cardToken;

    @Column(nullable = false)
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HoldStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "settled_at")
    private Instant settledAt; // set when CONFIRMED, used by the registry

    protected Hold() {}

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
