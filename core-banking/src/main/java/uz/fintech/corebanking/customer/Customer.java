package uz.fintech.corebanking.customer;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType type;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String phone;


    @Column(name = "document_number", nullable = false, unique = true)
    private String documentNumber;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Customer() {
    }

    public Customer(CustomerType type, String fullName, String phone, String documentNumber, Instant createdAt) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.fullName = fullName;
        this.phone = phone;
        this.documentNumber = documentNumber;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public CustomerType getType() {
        return type;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
