package uz.payming.cardprocessing.service;

import uz.payming.cardprocessing.domain.Credit;
import uz.payming.cardprocessing.domain.Hold;

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
                c.requestId(),
                c.processingTxId(),
                null,
                c.creditId(),
                c.cardToken(),
                c.amount(),
                "COMPLETED",
                c.createdAt()
        );
    }
}
