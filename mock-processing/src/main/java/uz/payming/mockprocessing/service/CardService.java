package uz.payming.mockprocessing.service;

import org.springframework.stereotype.Service;
import uz.payming.mockprocessing.domain.Card;
import uz.payming.mockprocessing.domain.CardStatus;
import uz.payming.mockprocessing.store.CardStore;

import java.time.Clock;
import java.time.Duration;
import java.time.YearMonth;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CardService {

    private static final Duration OTP_TTL = Duration.ofMinutes(2);
    private static final int MAX_OTP_ATTEMPTS = 3;
    static final String FIXED_OTP = "123456";

    private final CardStore cards;
    private final Clock clock;
    private final Map<UUID, PendingBinding> pending = new ConcurrentHashMap<>();

    public CardService(CardStore cards, Clock clock) {
        this.cards = cards;
        this.clock = clock;
    }

    public PendingBinding startBinding(String pan, YearMonth expiry, String phone) {
        Card card = cards.findByPan(pan)
                .orElseThrow(() -> new ProcessingException(ErrorCode.CARD_NOT_FOUND, "Card not found"));

        if (!card.getExpiry().equals(expiry)) {
            throw new ProcessingException(ErrorCode.CARD_NOT_FOUND, "Card not found");
        }

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new ProcessingException(ErrorCode.CARD_BLOCKED, "Card is blocked by issuer");
        }

        if (card.isExpired(YearMonth.now(clock))) {
            throw new ProcessingException(ErrorCode.CARD_EXPIRED, "Card is expired");
        }

        if (!card.getPhone().equals(phone)) {
            throw new ProcessingException(ErrorCode.PHONE_MISMATCH, "Phone does not match");
        }

        PendingBinding binding = new PendingBinding(
                UUID.randomUUID(), card.getToken(), FIXED_OTP, clock.instant().plus(OTP_TTL));
        pending.put(binding.tempId(), binding);
        return binding;
    }

    /** cards/verify: check the OTP, returns the bound card. */
    public Card verify(UUID tempId, String otp) {
        PendingBinding binding = pending.get(tempId);
        if (binding == null) {
            throw new ProcessingException(ErrorCode.TEMP_ID_NOT_FOUND, "Unknown temp_id");
        }

        if (clock.instant().isAfter(binding.expiresAt())) {
            pending.remove(tempId);
            throw new ProcessingException(ErrorCode.OTP_EXPIRED, "OTP expired, start over");
        }

        if (!binding.otp().equals(otp)) {
            if (binding.registerFailedAttempt() >= MAX_OTP_ATTEMPTS) {
                pending.remove(tempId);
                throw new ProcessingException(ErrorCode.TOO_MANY_ATTEMPTS, "Too many failed attempts");
            }

            throw new ProcessingException(ErrorCode.INVALID_OTP, "Invalid OTP");
        }
        pending.remove(tempId);
        return cards.findByToken(binding.cardToken())
                .orElseThrow(() -> new ProcessingException(ErrorCode.CARD_NOT_FOUND, "Card not found"));
    }

    /** cards/{token} */
    public Card info(UUID token) {
        return cards.findByToken(token)
                .orElseThrow(() -> new ProcessingException(ErrorCode.CARD_NOT_FOUND, "Card not found"));
    }

    public String maskPhone(UUID cardToken) {
        String phone = info(cardToken).getPhone();
        return phone.substring(0, 6) + "***" + phone.substring(phone.length() - 4);
    }


}
