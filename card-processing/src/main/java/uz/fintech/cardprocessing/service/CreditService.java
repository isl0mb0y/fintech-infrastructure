package uz.fintech.cardprocessing.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.fintech.cardprocessing.domain.Card;
import uz.fintech.cardprocessing.domain.CardStatus;
import uz.fintech.cardprocessing.domain.Credit;
import uz.fintech.cardprocessing.repository.CardRepository;
import uz.fintech.cardprocessing.repository.CreditRepository;

import java.time.Clock;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class CreditService {
    private final CardRepository cardRepository;
    private final CreditRepository creditRepository;
    private final TxIdGenerator txIdGenerator;
    private final Clock clock;

    public CreditService(CardRepository cardRepository, CreditRepository creditRepository, TxIdGenerator txIdGenerator, Clock clock) {
        this.cardRepository = cardRepository;
        this.creditRepository = creditRepository;
        this.txIdGenerator = txIdGenerator;
        this.clock = clock;
    }

    @Transactional
    public Credit create(UUID requestId, UUID cardToken, long amount) {
        Credit existing = creditRepository.findByRequestId(requestId).orElse(null);
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

        Credit credit = new Credit(requestId, txIdGenerator.next(), cardToken, amount, clock.instant());
        card.setBalance(card.getBalance() + amount);
        card.setAvailable(card.getAvailable() + amount);
        return creditRepository.save(credit);
    }
}
