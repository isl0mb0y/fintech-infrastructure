package uz.payming.cardprocessing.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.payming.cardprocessing.domain.Card;
import uz.payming.cardprocessing.domain.CardStatus;
import uz.payming.cardprocessing.domain.Hold;
import uz.payming.cardprocessing.domain.HoldStatus;
import uz.payming.cardprocessing.repository.CardRepository;
import uz.payming.cardprocessing.repository.HoldRepository;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class HoldService {

    static final Duration HOLD_TTL = Duration.ofMinutes(10);

    private final CardRepository cardRepository;
    private final HoldRepository holdRepository;
    private final TxIdGenerator txIdGenerator;
    private final Clock clock;

    public HoldService(CardRepository cardRepository, HoldRepository holdRepository, TxIdGenerator txIdGenerator, Clock clock) {
        this.cardRepository = cardRepository;
        this.holdRepository = holdRepository;
        this.txIdGenerator = txIdGenerator;
        this.clock = clock;
    }

    @Transactional
    public Hold create(UUID requestId, UUID cardToken, long amount) {
        Hold existing = holdRepository.findByRequestId(requestId).orElse(null);
        if (existing != null) {
            if (existing.getCardToken().equals(cardToken) && existing.getAmount() == amount) {
                return existing;
            }
            throw new ProcessingException(ErrorCode.REQUEST_ID_CONFLICT, "request_id already used with different parameters");
        }

        Card card = cardRepository.findByTokenForUpdate(cardToken)
                .orElseThrow(() -> new ProcessingException(ErrorCode.CARD_NOT_FOUND, "Card not found"));
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
        Hold hold = new Hold(
                requestId,
                txIdGenerator.next(),
                cardToken,
                amount,
                now,
                now.plus(HOLD_TTL)
        );
        card.setAvailable(card.getAvailable() - amount);
        return holdRepository.save(hold);
    }

    @Transactional
    public Hold confirm(UUID holdId) {
        Hold hold = find(holdId);
        Card card = cardRepository.findByTokenForUpdate(hold.getCardToken()).orElseThrow();

        expireIfDue(hold, card);
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

    @Transactional
    public Hold reverse(UUID holdId) {
        Hold hold = find(holdId);
        Card card = cardRepository.findByTokenForUpdate(hold.getCardToken()).orElseThrow();

        expireIfDue(hold, card);
        switch (hold.getStatus()) {
            case REVERSED, EXPIRED -> {return hold;}
            case CONFIRMED -> throw new ProcessingException(ErrorCode.HOLD_CONFIRMED, "Hold is already confirmed");
            case ACTIVE -> {
                card.setAvailable(card.getAvailable() + hold.getAmount());
                hold.setStatus(HoldStatus.REVERSED);
                return hold;
            }
        }

        throw new IllegalStateException("Unreachable");
    }

    /** Caller must hold the card now lock. */
    void expireIfDue(Hold hold, Card card) {
        if (hold.isActive() && clock.instant().isAfter(hold.getExpiresAt())) {
            card.setAvailable(card.getAvailable() + hold.getAmount());
            hold.setStatus(HoldStatus.EXPIRED);
        }
    }

    private Hold find(UUID holdId) {
        return holdRepository.findById(holdId)
                .orElseThrow(() -> new ProcessingException(ErrorCode.HOLD_NOT_FOUND, "Hold not found"));
    }
}
