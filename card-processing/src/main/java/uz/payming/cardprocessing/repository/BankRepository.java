package uz.payming.cardprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.payming.cardprocessing.domain.Bank;

public interface BankRepository extends JpaRepository<Bank, String> {
}
