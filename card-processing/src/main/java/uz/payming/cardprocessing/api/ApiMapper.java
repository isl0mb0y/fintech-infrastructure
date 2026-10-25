package uz.payming.cardprocessing.api;


import uz.payming.cardprocessing.api.generated.model.*;
import uz.payming.cardprocessing.domain.Card;
import uz.payming.cardprocessing.domain.Credit;
import uz.payming.cardprocessing.domain.Hold;
import uz.payming.cardprocessing.service.RegistryEntry;
import uz.payming.cardprocessing.service.Transaction;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Converts domain objects to generated API models. */
final class ApiMapper {

    private static final DateTimeFormatter EXPIRY = DateTimeFormatter.ofPattern("MM/yy");

    private ApiMapper() {}

    static CardDto toDto(Card c) {
        return new CardDto()
                .token(c.getToken())
                .maskedPan(c.maskedPan())
                .owner(c.getOwner())
                .network(CardDto.NetworkEnum.valueOf(c.getNetwork().name()))
                .expiry(c.getExpiry().format(EXPIRY))
                .balance(c.getBalance())
                .available(c.getAvailable())
                .status(CardDto.StatusEnum.valueOf(c.getStatus().name()));
    }

    static OffsetDateTime at(Instant i) {
        return i.atOffset(ZoneOffset.UTC);
    }

    static HoldDto toDto(Hold h) {
        return new HoldDto()
                .holdId(h.getHoldId())
                .requestId(h.getRequestId())
                .processingTxId(h.getProcessingTxId())
                .cardToken(h.getCardToken())
                .amount(h.getAmount())
                .status(HoldDto.StatusEnum.valueOf(h.getStatus().name()))
                .createdAt(at(h.getCreatedAt()))
                .expiresAt(at(h.getExpiresAt()));
    }

    static CreditDto toDto(Credit c) {
        return new CreditDto()
                .creditId(c.creditId())
                .requestId(c.requestId())
                .processingTxId(c.processingTxId())
                .cardToken(c.cardToken())
                .amount(c.amount())
                .status(CreditDto.StatusEnum.COMPLETED)
                .createdAt(at(c.createdAt()));
    }

    static TransactionDto toDto(Transaction t) {
        return new TransactionDto()
                .type(TransactionDto.TypeEnum.valueOf(t.type().name()))
                .requestId(t.requestId())
                .processingTxId(t.processingTxId())
                .holdId(t.holdId())
                .creditId(t.creditId())
                .cardToken(t.cardToken())
                .amount(t.amount())
                .status(TransactionDto.StatusEnum.valueOf(t.status()))
                .createdAt(at(t.createdAt()));
    }

    static RegistryDto toDto(LocalDate date, List<RegistryEntry> entries) {
        return new RegistryDto()
                .date(date)
                .entries(entries.stream().map(e -> new RegistryEntryDto()
                        .processingTxId(e.processingTxId())
                        .requestId(e.requestId())
                        .type(RegistryEntryDto.TypeEnum.valueOf(e.type().name()))
                        .cardToken(e.cardToken())
                        .amount(e.amount())
                        .settledAt(at(e.settledAt()))
                ).toList());
    }


}
