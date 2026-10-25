package uz.payming.mockprocessing.api;


import uz.payming.mockprocessing.api.generated.model.CardDto;
import uz.payming.mockprocessing.api.generated.model.CreditDto;
import uz.payming.mockprocessing.api.generated.model.HoldDto;
import uz.payming.mockprocessing.domain.Card;
import uz.payming.mockprocessing.domain.Credit;
import uz.payming.mockprocessing.domain.Hold;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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

}
