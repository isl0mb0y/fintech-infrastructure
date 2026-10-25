package uz.payming.cardprocessing.store;

import org.springframework.stereotype.Repository;
import uz.payming.cardprocessing.domain.Card;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCardStore implements CardStore {

    private final Map<UUID, Card> byToken = new ConcurrentHashMap<>();
    private final Map<String, Card> byPan = new ConcurrentHashMap<>();

    @Override
    public Optional<Card> findByToken(UUID token) {
        return Optional.ofNullable(byToken.get(token));
    }

    @Override
    public Optional<Card> findByPan(String pan) {
        return Optional.ofNullable(byPan.get(pan));
    }

    @Override
    public void save(Card card) {
        byToken.put(card.getToken(), card);
        byPan.put(card.getPan(), card);
    }
}
