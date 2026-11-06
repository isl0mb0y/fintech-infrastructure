package uz.fintech.corebanking.ledger;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** One money movement. Its postings always sum to zero. Never updated or deleted. */
@Entity
@Table(name = "journal_entries")
public class JournalEntry {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryType type;

    @Column(nullable = false, unique = true)
    private String reference;    // idempotency key from the caller

    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected JournalEntry() {}

    public JournalEntry(EntryType type, String reference, String description, Instant createdAt) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.reference = reference;
        this.description = description;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public EntryType getType() {
        return type;
    }

    public String getReference() {
        return reference;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
