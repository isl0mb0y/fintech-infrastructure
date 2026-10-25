package uz.payming.cardprocessing.service;

import org.springframework.stereotype.Service;
import uz.payming.cardprocessing.domain.Card;
import uz.payming.cardprocessing.domain.CardStatus;
import uz.payming.cardprocessing.domain.Credit;
import uz.payming.cardprocessing.store.CardStore;
import uz.payming.cardprocessing.store.OperationStore;

import java.time.Clock;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class CreditService {

    private final CardStore cards;
    private final OperationStore operations;
    private final TxIdGenerator txIds;
    private final Clock clock;

    public CreditService(CardStore cards, OperationStore operations, TxIdGenerator txIds, Clock clock) {
        this.cards = cards;
        this.operations = operations;
        this.txIds = txIds;
        this.clock = clock;
    }

    /** POST /credit */
    public Credit create(UUID requestId, UUID cardToken, long amount) {
        Credit existing = operations.findCreditByRequestId(requestId).orElse(null);
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

            Credit credit = new Credit(requestId, txIds.next(), cardToken, amount, clock.instant());
            card.setBalance(card.getBalance() + amount);
            card.setAvailable(card.getAvailable() + amount);
            operations.save(credit);
            return credit;
        }
    }
}
