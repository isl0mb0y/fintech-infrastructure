package uz.fintech.cardprocessing.service;

import uz.fintech.cardprocessing.domain.Credit;
import uz.fintech.cardprocessing.domain.Hold;

import java.time.Instant;
import java.util.UUID;

public record Transaction(
        Type type,
        UUID requestId,
        String processingTxId,
        UUID holdId,  // null for CREDIT
        UUID creditId, // null for HOLD
        UUID cardToken,
        long amount,
        String status,
        Instant createdAt

) {
    public enum Type {HOLD, CREDIT}

    public static Transaction of(Hold h) {
        return new Transaction(
                Type.HOLD,
                h.getRequestId(),
                h.getProcessingTxId(),
                h.getHoldId(),
                null,
                h.getCardToken(),
                h.getAmount(),
                h.getStatus().name(),
                h.getCreatedAt()
                );
    }

    public static Transaction of(Credit c) {
        return new Transaction(
                Type.CREDIT,
                c.getRequestId(),
                c.getProcessingTxId(),
                null,
                c.getCreditId(),
                c.getCardToken(),
                c.getAmount(),
                "COMPLETED",
                c.getCreatedAt()
        );
    }
}
