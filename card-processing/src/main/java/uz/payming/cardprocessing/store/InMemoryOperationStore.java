package uz.payming.cardprocessing.store;

import org.springframework.stereotype.Repository;
import uz.payming.cardprocessing.domain.Credit;
import uz.payming.cardprocessing.domain.Hold;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOperationStore implements OperationStore {

    private final Map<UUID, Hold> holdsById = new ConcurrentHashMap<>();
    private final Map<UUID, Hold> holdsByRequestId = new ConcurrentHashMap<>();
    private final Map<UUID, Credit> creditsByRequestId = new ConcurrentHashMap<>();

    @Override
    public Optional<Hold> findHoldById(UUID holdId) {
        return Optional.ofNullable(holdsById.get(holdId));
    }

    @Override
    public Optional<Hold> findHoldByRequestId(UUID requestId) {
        return Optional.ofNullable(holdsByRequestId.get(requestId));
    }

    @Override
    public Optional<Credit> findCreditByRequestId(UUID requestId) {
        return Optional.ofNullable(creditsByRequestId.get(requestId));
    }

    @Override
    public Collection<Hold> allHolds() {
        return holdsById.values();
    }

    @Override
    public Collection<Credit> allCredits() {
        return creditsByRequestId.values();
    }

    @Override
    public void save(Hold hold) {
        holdsById.put(hold.getHoldId(), hold);
        holdsByRequestId.put(hold.getRequestId(), hold);
    }

    @Override
    public void save(Credit credit) {
        creditsByRequestId.put(credit.requestId(), credit);
    }
}
