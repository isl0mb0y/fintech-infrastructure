package uz.fintech.corebanking.account;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(unique = true)
    private String code;

    @Column(name = "customer_id")
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(nullable = false)
    private long balance; // tiyin, cached sum of postings

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Account() {}

    /** Opens a customer account. Internal accounts are created by migrations only. */
    public static Account openForCustomer(UUID customerId, String number, String currency, Instant createdAt) {
        Account a = new Account();
        a.id = UUID.randomUUID();
        a.number = number;
        a.customerId = customerId;
        a.type = AccountType.CUSTOMER;
        a.currency = currency;
        a.status = AccountStatus.OPEN;
        a.balance = 0;
        a.createdAt = createdAt;
        return a;
    }

    /** Called only by the ledger, inside the posting transaction. */
    public void applyPosting(long amount) {
        this.balance += amount;
    }

    public boolean isOpen() {
        return status == AccountStatus.OPEN;
    }

    public UUID getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getCode() {
        return code;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public AccountType getType() {
        return type;
    }

    public String getCurrency() {
        return currency;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public long getBalance() {
        return balance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
