package uz.fintech.corebanking.account;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByNumber(String number);
    Optional<Account> findByCode(String code);
    List<Account> findByCustomerId(UUID customerId);


    /** SELECT ... FOR UPDATE, ordered by id to avoid deadlocks. Must be called inside a transaction. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id in :ids order by a.id")
    List<Account> findAllByIdForUpdate(List<UUID> ids);
}
