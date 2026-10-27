package uz.fintech.cardprocessing.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;
import uz.fintech.cardprocessing.domain.Bank;
import uz.fintech.cardprocessing.domain.Card;
import uz.fintech.cardprocessing.domain.CardStatus;
import uz.fintech.cardprocessing.repository.BankRepository;
import uz.fintech.cardprocessing.repository.CardRepository;

import java.time.YearMonth;

/** Test banks and cards for local runs. Inserted only into an empty database. */
@Configuration
public class SeedData {

    @Bean
    ApplicationRunner seed(BankRepository bankRepository, CardRepository cardRepository,
                           TransactionTemplate tx) {
        return args -> tx.executeWithoutResult(status -> {
            if (bankRepository.count() > 0) {
                return;
            }

            bankRepository.save(new Bank("KAPITAL", "Kapitalbank", "00974", "29801000100000974001"));
            bankRepository.save(new Bank("IPOTEKA", "Ipoteka Bank", "00873", "29801000100000873001"));
            bankRepository.save(new Bank("HAMKOR", "Hamkorbank", "00083", "29801000100000083001"));

            cardRepository.save(new Card("KAPITAL", "20216000100000000001", "8600123412341234",
                    "+998901234567", "A. ALIYEV", YearMonth.of(2028, 12), 150_000_000L));
            cardRepository.save(new Card("KAPITAL", "20216000100000000002", "8600111122223333",
                    "+998901234568", "B. KARIMOV", YearMonth.of(2027, 6), 50_000_000L));
            cardRepository.save(new Card("IPOTEKA", "20216000200000000001", "9860123412341234",
                    "+998901234569", "S. RASHIDOVA", YearMonth.of(2029, 3), 200_000_000L));
            cardRepository.save(new Card("HAMKOR", "20216000300000000001", "9860111122223333",
                    "+998901234570", "D. TOSHEV", YearMonth.of(2028, 1), 1_000_000L));

            Card blocked = new Card("IPOTEKA", "20216000200000000002", "8600999988887777",
                    "+998901234571", "X. BLOCKED", YearMonth.of(2028, 12), 10_000_000L);
            blocked.setStatus(CardStatus.BLOCKED);
            cardRepository.save(blocked);

            cardRepository.save(new Card("HAMKOR", "20216000300000000002", "9860999988887777",
                    "+998901234572", "Y. EXPIRED", YearMonth.of(2024, 1), 10_000_000L));
        });
    }
}
