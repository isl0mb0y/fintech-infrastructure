package uz.fintech.cardprocessing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "banks")
public class Bank {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String bic;

    @Column(name = "settlement_account", nullable = false)
    private String settlementAccount;

    protected Bank() {}

    public Bank(String id, String name, String bic, String settlementAccount) {
        this.id = id;
        this.name = name;
        this.bic = bic;
        this.settlementAccount = settlementAccount;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBic() {
        return bic;
    }

    public String getSettlementAccount() {
        return settlementAccount;
    }
}
