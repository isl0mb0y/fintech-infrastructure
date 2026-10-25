package uz.payming.cardprocessing.domain;

import java.time.Instant;
import java.util.UUID;

public record Credit(
        UUID creditId,
        UUID requestId,
        String processingTxId,
        UUID cardToken,
        long amount,
        Instant createdAt

) {
    public static Credit create(
            UUID requestId,
            String processingTxId,
            UUID cardToken,
            long amount,
            Instant createdAt
    ) {
        return new Credit(UUID.randomUUID(), requestId, processingTxId, cardToken, amount, createdAt);
    }
}
