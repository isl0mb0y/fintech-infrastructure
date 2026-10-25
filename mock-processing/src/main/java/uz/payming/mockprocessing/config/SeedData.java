package uz.payming.mockprocessing.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.payming.mockprocessing.domain.Card;
import uz.payming.mockprocessing.domain.CardStatus;
import uz.payming.mockprocessing.store.CardStore;

import java.time.YearMonth;

@Configuration
public class SeedData {
    @Bean
    ApplicationRunner seed(CardStore cards) {
        return args -> {
            cards.save(new Card("8600123412341234", "+998901234567", "A. ALIYEV", YearMonth.of(2028, 12), 150_000_000L));
            cards.save(new Card("8600111122223333", "+998901234568", "B. KARIMOV", YearMonth.of(2027, 6), 50_000_000L));
            cards.save(new Card("9860123412341234", "+998901234569", "S. RASHIDOVA", YearMonth.of(2029, 3), 200_000_000L));
            cards.save(new Card("9860111122223333", "+998901234570", "D. TOSHEV", YearMonth.of(2028, 1), 1_000_000L));

            Card blocked = new Card("8600999988887777", "+998901234571", "X. BLOCKED", YearMonth.of(2028, 12), 10_000_000L);
            blocked.setStatus(CardStatus.BLOCKED);
            cards.save(blocked);
            cards.save(new Card("9860999988887777", "+998901234572", "Y. EXPIRED", YearMonth.of(2024, 1), 10_000_000L));

        };
    }
}
