package uz.fintech.cardprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.fintech.cardprocessing.domain.Bank;

public interface BankRepository extends JpaRepository<Bank, String> {
}
