package uz.fintech.cardprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.fintech.cardprocessing.domain.Hold;
import uz.fintech.cardprocessing.domain.HoldStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HoldRepository extends JpaRepository<Hold, UUID> {

    Optional<Hold> findByRequestId(UUID requestId);

    List<Hold> findByStatusAndExpiresAtBefore(HoldStatus status, Instant before);

    List<Hold> findByStatusAndSettledAtGreaterThanEqualAndSettledAtLessThan(
            HoldStatus status, Instant from, Instant to
    );
}
