package uz.payming.mockprocessing.store;

import uz.payming.mockprocessing.domain.Card;

import java.util.Optional;
import java.util.UUID;

public interface CardStore {
    Optional<Card> findByToken(UUID token);
    Optional<Card> findByPan(String pan);
    void save(Card card);
}
