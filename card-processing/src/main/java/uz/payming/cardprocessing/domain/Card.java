package uz.payming.cardprocessing.domain;

import jakarta.persistence.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "cards")
public class Card {

    private static final DateTimeFormatter EXPIRY = DateTimeFormatter.ofPattern("MM/yy");

    @Id
    private UUID token;

    @Column(name = "bank_id", nullable = false)
    private String bankId;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(nullable = false, unique = true)
    private String pan;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Network network;

    @Column(nullable = false)
    private String expiry; // MM/yy

    @Column(nullable = false)
    private long balance;      // tiyin

    @Column(nullable = false)
    private long available; // tiyin, balance minus active holds

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;

    protected Card() {}

    public Card(String bankId,
                String accountNumber,
                String pan,
                String phone,
                String owner,
                YearMonth expiry,
                long balance
                ) {

        this.token = UUID.randomUUID();
        this.bankId = bankId;
        this.accountNumber = accountNumber;
        this.pan = pan;
        this.phone = phone;
        this.owner = owner;
        this.network = Network.fromPan(pan);
        this.expiry = expiry.format(EXPIRY);
        this.balance = balance;
        this.available = balance;
        this.status = CardStatus.ACTIVE;
    }

    public String maskedPan() {
        return pan.substring(0, 6) + "******" + pan.substring(12);
    }

    public boolean isExpired(YearMonth now) {
        return getExpiry().isBefore(now);
    }

    public UUID getToken() {
        return token;
    }
    public String getBankId() {
        return bankId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPan() {
        return pan;
    }
    public String getPhone() {
        return phone;
    }
    public String getOwner() {
        return owner;
    }
    public Network getNetwork() {
        return network;
    }
    public YearMonth getExpiry() {
        return YearMonth.parse(expiry, EXPIRY);
    }
    public long getBalance() {
        return balance;
    }
    public long getAvailable() {
        return available;
    }
    public CardStatus getStatus() {
        return status;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
    public void setAvailable(long available) {
        this.available = available;
    }
    public void setStatus(CardStatus status) {
        this.status = status;
    }
}
