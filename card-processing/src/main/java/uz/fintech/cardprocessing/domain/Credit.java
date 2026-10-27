package uz.fintech.cardprocessing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "credits")
public class Credit{

    @Id
    @Column(name = "credit_id")
    private UUID creditId;

    @Column(name = "request_id", nullable = false, unique = true)
    private UUID requestId;

    @Column(name = "processing_tx_id", nullable = false, unique = true)
    private String processingTxId;

    @Column(name = "card_token", nullable = false)
    private UUID cardToken;

    @Column(nullable = false)
    private long amount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Credit() {}

    public Credit(UUID requestId, String processingTxId, UUID cardToken, long amount, Instant createdAt) {
        this.creditId = UUID.randomUUID();
        this.requestId = requestId;
        this.processingTxId = processingTxId;
        this.cardToken = cardToken;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public UUID getCreditId() {
        return creditId;
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
}
