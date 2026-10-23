package uz.payming.mockprocessing.domain;

import java.time.YearMonth;
import java.util.UUID;

public class Card {
    private final UUID token;
    private final String pan;
    private final String phone;
    private final String owner;
    private final Network network;
    private final YearMonth expiry;
    private long balance;      // tiyin
    private long available; // tiyin, balance minus active holds
    private CardStatus status;

    public Card(String pan, String phone, String owner, YearMonth expiry, long balance) {
        this.token = UUID.randomUUID();
        this.pan = pan;
        this.phone = phone;
        this.owner = owner;
        this.network = Network.fromPan(pan);
        this.expiry = expiry;
        this.balance = balance;
        this.available = balance;
        this.status = CardStatus.ACTIVE;
    }

    public String maskedPan() {
        return pan.substring(0, 6) + "******" + pan.substring(12);
    }

    public boolean isExpired(YearMonth now) {
        return expiry.isBefore(now);
    }

    public UUID getToken() {
        return token;
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
        return expiry;
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
