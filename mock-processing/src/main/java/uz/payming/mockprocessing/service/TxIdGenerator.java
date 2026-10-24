package uz.payming.mockprocessing.service;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TxIdGenerator {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final Clock clock;
    private final AtomicLong counter = new AtomicLong();

    public TxIdGenerator(Clock clock) {
        this.clock = clock;
    }

    public String next() {
        String date = LocalDate.now(clock).format(DATE);
        return "UZ-%s-%06d".formatted(date, counter.incrementAndGet());
    }
}
