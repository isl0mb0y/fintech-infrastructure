package uz.payming.cardprocessing.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.payming.cardprocessing.domain.Card;
import uz.payming.cardprocessing.domain.CardStatus;
import uz.payming.cardprocessing.store.CardStore;

import java.time.YearMonth;

@Configuration
public class SeedData {
    @Bean
    ApplicationRunner seed(CardStore cards) {
        return null;
    }
}
