package uz.fintech.corebanking.ledger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PostingRepository extends JpaRepository<Posting, UUID> {
    List<Posting> findByEntryId(UUID entryId);

    @Query("select coalesce(sum(p.amount), 0) from Posting p where p.accountId = :accountId")
    long sumByAccountId(UUID accountId);
}
