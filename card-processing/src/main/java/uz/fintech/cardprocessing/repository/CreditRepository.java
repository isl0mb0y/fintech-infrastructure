package uz.fintech.cardprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.fintech.cardprocessing.domain.Credit;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditRepository extends JpaRepository<Credit, UUID> {
    Optional<Credit> findByRequestId(UUID requestId);

    List<Credit> findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(Instant from, Instant to);

}
