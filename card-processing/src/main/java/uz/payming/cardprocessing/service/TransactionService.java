package uz.payming.cardprocessing.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.payming.cardprocessing.domain.HoldStatus;
import uz.payming.cardprocessing.repository.CreditRepository;
import uz.payming.cardprocessing.repository.HoldRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class TransactionService {

    private final HoldRepository holdRepository;
    private final CreditRepository creditRepository;

    public TransactionService(HoldRepository holdRepository, CreditRepository creditRepository) {
        this.holdRepository = holdRepository;
        this.creditRepository = creditRepository;
    }

    /** GET /transactions/{request_id}: a hold or a credit, whichever was recorded for this id. */
    @Transactional(readOnly = true)
    public Transaction inquiry(UUID requestId) {
        return holdRepository.findByRequestId(requestId)
                .map(Transaction::of)
                .or(() -> creditRepository.findByRequestId(requestId).map(Transaction::of))
                .orElseThrow(() -> new ProcessingException(
                        ErrorCode.TRANSACTION_NOT_FOUND, "No operation with this request_id"));
    }

    /** GET /registry?date=: confirmed holds (DEBIT) and credits (CREDIT) settled on the date, UTC. */
    @Transactional(readOnly = true)
    public List<RegistryEntry> registry(LocalDate date) {
        Instant from = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        Stream<RegistryEntry> debits = holdRepository
                .findByStatusAndSettledAtGreaterThanEqualAndSettledAtLessThan(HoldStatus.CONFIRMED, from, to)
                .stream()
                .map(RegistryEntry::debit);

        Stream<RegistryEntry> credits = creditRepository
                .findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(from, to)
                .stream()
                .map(RegistryEntry::credit);

        return Stream.concat(debits, credits)
                .sorted(Comparator.comparing(RegistryEntry::settledAt))
                .toList();
    }
}
