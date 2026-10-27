package uz.fintech.cardprocessing.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.fintech.cardprocessing.domain.Card;
import uz.fintech.cardprocessing.domain.CardStatus;
import uz.fintech.cardprocessing.repository.CardRepository;

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

    private final CardRepository cardRepository;
    private final Clock clock;
    private final Map<UUID, PendingBinding> pending = new ConcurrentHashMap<>();

    public CardService(CardRepository cardRepository, Clock clock) {
        this.cardRepository = cardRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public PendingBinding startBinding(String pan, YearMonth expiry, String phone) {
        Card card = cardRepository.findByPan(pan)
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
    @Transactional(readOnly = true)
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
        return cardRepository.findById(binding.cardToken())
                .orElseThrow(() -> new ProcessingException(ErrorCode.CARD_NOT_FOUND, "Card not found"));
    }

    /** cards/{token} */
    @Transactional(readOnly = true)
    public Card info(UUID token) {
        return cardRepository.findById(token)
                .orElseThrow(() -> new ProcessingException(ErrorCode.CARD_NOT_FOUND, "Card not found"));
    }

    public String maskPhone(UUID cardToken) {
        String phone = info(cardToken).getPhone();
        return phone.substring(0, 6) + "***" + phone.substring(phone.length() - 4);
    }


}
