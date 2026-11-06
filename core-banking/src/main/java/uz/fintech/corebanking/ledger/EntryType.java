package uz.fintech.corebanking.ledger;

public enum EntryType {
    DEPOSIT, // cash in: CASH -> customer account
    WITHDRAWAL, // cash out: customer account -> CASH
    TRANSFER // customer account -> customer account
}
