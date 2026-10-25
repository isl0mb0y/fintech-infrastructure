package uz.payming.cardprocessing.service;

import org.springframework.stereotype.Service;
import uz.payming.cardprocessing.domain.Card;
import uz.payming.cardprocessing.domain.CardStatus;
import uz.payming.cardprocessing.domain.Hold;
import uz.payming.cardprocessing.domain.HoldStatus;
import uz.payming.cardprocessing.store.CardStore;
import uz.payming.cardprocessing.store.OperationStore;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class HoldService {

    static final Duration HOLD_TTL = Duration.ofMinutes(10);

    private final CardStore cards;
    private final OperationStore operations;
    private final TxIdGenerator txIds;
    private final Clock clock;

    public HoldService(CardStore cards, OperationStore operations, TxIdGenerator txIds, Clock clock) {
        this.cards = cards;
        this.operations = operations;
        this.txIds = txIds;
        this.clock = clock;
    }

    public Hold create(UUID requestId, UUID cardToken, long amount) {
        Hold existing = operations.findHoldByRequestId(requestId).orElse(null);
        if (existing != null) {
            if (existing.getCardToken().equals(cardToken) && existing.getAmount() == amount) {
                return existing;
            }

            throw new ProcessingException(ErrorCode.REQUEST_ID_CONFLICT,
                    "request_id already used with different parameters");
        }

        Card card = cards.findByToken(cardToken)
                .orElseThrow(() -> new ProcessingException(ErrorCode.CARD_NOT_FOUND, "Card not found"));
        synchronized (card) {
            if (card.getStatus() == CardStatus.BLOCKED) {
                throw new ProcessingException(ErrorCode.CARD_BLOCKED, "Card is blocked");
            }

            if (card.isExpired(YearMonth.now(clock))) {
                throw new ProcessingException(ErrorCode.CARD_EXPIRED, "Card is expired");
            }

            if (card.getAvailable() < amount) {
                throw new ProcessingException(ErrorCode.INSUFFICIENT_FUNDS, "Insufficient funds");
            }

            Instant now = clock.instant();
            Hold hold = new Hold(requestId, txIds.next(), cardToken, amount, now, now.plus(HOLD_TTL));
            card.setAvailable(card.getAvailable() - amount);
            operations.save(hold);
            return hold;
        }
    }

    /** POST /hold/{id}/confirm */
    public Hold confirm(UUID holdId) {
        Hold hold = find(holdId);
        Card card = cards.findByToken(hold.getCardToken()).orElseThrow();

        synchronized (card) {
            expireIfDue(hold);
            switch (hold.getStatus()) {
                case CONFIRMED -> {return hold;}
                case EXPIRED -> throw new ProcessingException(ErrorCode.HOLD_EXPIRED, "Hold has expired");
                case REVERSED -> throw new ProcessingException(ErrorCode.HOLD_REVERSED, "Hold was reversed");
                case ACTIVE -> {
                    card.setBalance(card.getBalance() - hold.getAmount());
                    hold.setStatus(HoldStatus.CONFIRMED);
                    hold.setSettledAt(clock.instant());
                    return hold;
                }
            }

            throw new IllegalStateException("Unreachable");
        }
    }

    /** POST /hold/{id}/reverse */
    public Hold reverse(UUID holdId) {
        Hold hold = find(holdId);
        Card card = cards.findByToken(hold.getCardToken()).orElseThrow();

        synchronized (card) {
            expireIfDue(hold);
            switch (hold.getStatus()) {
                case REVERSED, EXPIRED -> {return hold;}
                case CONFIRMED -> throw new ProcessingException(ErrorCode.HOLD_CONFIRMED, "Hold is already confirmed.");
                case ACTIVE -> {
                    card.setAvailable(card.getAvailable() + hold.getAmount());
                    hold.setStatus(HoldStatus.REVERSED);
                    return hold;
                }
            }

            throw new IllegalStateException("Unreachable");
        }
    }

    /** Called by the expiry job and lazily on confirm/reverse. Caller must hold the card lock. */
    void expireIfDue(Hold hold) {
        if (hold.isActive() && clock.instant().isAfter(hold.getExpiresAt())) {
            Card card = cards.findByToken(hold.getCardToken()).orElseThrow();
            card.setAvailable(card.getAvailable() + hold.getAmount());
            hold.setStatus(HoldStatus.EXPIRED);
        }
    }

    private Hold find(UUID holdId) {
        return operations.findHoldById(holdId)
                .orElseThrow(() -> new ProcessingException(ErrorCode.HOLD_NOT_FOUND, "Hold not found"));
    }

}
