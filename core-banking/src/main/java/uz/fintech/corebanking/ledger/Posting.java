package uz.fintech.corebanking.ledger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;


/** One side of a journal entry: an amount added to (+) or taken from (-) an account. */
@Entity
@Table(name = "postings")
public class Posting {

    @Id
    private UUID id;

    @Column(name = "entry_id", nullable = false)
    private UUID entryId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private long amount;   // tiyin, signed

    @Column(name = "value_date", nullable = false)
    private LocalDate valueDate;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Posting() {}

    public Posting(UUID entryId, UUID accountId, long amount, LocalDate valueDate, Instant createdAt) {
        this.id = UUID.randomUUID();
        this.entryId = entryId;
        this.accountId = accountId;
        this.amount = amount;
        this.valueDate = valueDate;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEntryId() {
        return entryId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public long getAmount() {
        return amount;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
