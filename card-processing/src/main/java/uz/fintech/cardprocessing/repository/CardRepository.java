package uz.fintech.cardprocessing.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import uz.fintech.cardprocessing.domain.Card;

import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    Optional<Card> findByPan(String pan);

    /** SELECT ... FOR UPDATE. Must be called inside a transaction. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Card c where c.token = :token")
    Optional<Card> findByTokenForUpdate(UUID token);
}
