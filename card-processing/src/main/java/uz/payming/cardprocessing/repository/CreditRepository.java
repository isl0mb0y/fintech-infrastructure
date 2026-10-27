package uz.payming.cardprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.payming.cardprocessing.domain.Credit;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditRepository extends JpaRepository<Credit, UUID> {
    Optional<Credit> findByRequestId(UUID requestId);

    List<Credit> findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(Instant from, Instant to);

}
