package uz.payming.cardprocessing.service;

import org.springframework.stereotype.Service;
import uz.payming.cardprocessing.domain.Hold;
import uz.payming.cardprocessing.domain.HoldStatus;
import uz.payming.cardprocessing.store.OperationStore;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class TransactionService {
    private final OperationStore operations;

    public TransactionService(OperationStore operations) {
        this.operations = operations;
    }

    /** GET /transactions/{request_id}: a hold or a credit, whichever was recorded for this id. */
    public Transaction inquiry(UUID requestId) {
        Optional<Hold> hold = operations.findHoldByRequestId(requestId);
        if (hold.isPresent()) {
            return Transaction.of(hold.get());
        }

        return operations.findCreditByRequestId(requestId)
                .map(Transaction::of)
                .orElseThrow(() -> new ProcessingException(
                        ErrorCode.TRANSACTION_NOT_FOUND,
                        "No operation with this request_id"
                        ));
    }

    /** GET /registry?date=: confirmed holds (DEBIT) and credits (CREDIT) settled on the date, UTC. */
    public List<RegistryEntry> registry(LocalDate date) {
        Instant from = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        Stream<RegistryEntry> debits = operations.allHolds().stream()
                .filter(h -> h.getStatus() == HoldStatus.CONFIRMED)
                .filter(h -> within(h.getSettledAt(), from, to))
                .map(RegistryEntry::debit);

        Stream<RegistryEntry> credits = operations.allCredits().stream()
                .filter(c -> within(c.createdAt(), from, to))
                .map(RegistryEntry::credit);

        return Stream.concat(debits, credits)
                .sorted((a, b) -> a.settledAt().compareTo(b.settledAt()))
                .toList();
    }

    private static boolean within(Instant t, Instant from, Instant to) {
        return !t.isBefore(from) && t.isBefore(to);
    }

}
