package uz.payming.mockprocessing.api;


import uz.payming.mockprocessing.api.generated.model.CardDto;
import uz.payming.mockprocessing.domain.Card;

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

}
