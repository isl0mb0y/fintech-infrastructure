package uz.payming.mockprocessing.service;

import uz.payming.mockprocessing.domain.Credit;
import uz.payming.mockprocessing.domain.Hold;

import java.time.Instant;
import java.util.UUID;

/** One line of the daily registry: money that actually moved. */
public record RegistryEntry(
        String processingTxId,
        UUID requestId,
        Type type,
        UUID cardToken,
        long amount,
        Instant settledAt
) {

    public enum Type {DEBIT, CREDIT}

    public static RegistryEntry debit(Hold h) {
        return new RegistryEntry(
                h.getProcessingTxId(),
                h.getRequestId(),
                Type.DEBIT,
                h.getCardToken(),
                h.getAmount(),
                h.getSettledAt()
        );
    }

    public static RegistryEntry credit(Credit c) {
        return new RegistryEntry(
                c.processingTxId(),
                c.requestId(),
                Type.CREDIT,
                c.cardToken(),
                c.amount(),
                c.createdAt()
        );
    }
}
