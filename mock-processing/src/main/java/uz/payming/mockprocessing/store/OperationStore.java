package uz.payming.mockprocessing.store;

import uz.payming.mockprocessing.domain.Credit;
import uz.payming.mockprocessing.domain.Hold;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface OperationStore {
    Optional<Hold> findHoldById(UUID holdId);
    Optional<Hold> findHoldByRequestId(UUID requestId);
    Optional<Credit> findCreditByRequestId(UUID requestId);
    Collection<Hold> allHolds();
    Collection<Credit> allCredits();
    void save(Hold hold);
    void save(Credit credit);
}
