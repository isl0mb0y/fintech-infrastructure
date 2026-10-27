package uz.fintech.cardprocessing.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Generates processing-side transaction ids like UZ-20261027-000123. */
@Component
public class TxIdGenerator {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;

    public TxIdGenerator(JdbcTemplate jdbcTemplate, Clock clock) {
        this.jdbcTemplate = jdbcTemplate;
        this.clock = clock;
    }

    public String next() {
        Long seq = jdbcTemplate.queryForObject("select nextval('processing_tx_seq')", Long.class);
        String date = LocalDate.now(clock).format(DATE);
        return "UZ-%s-%06d".formatted(date, seq);
    }
}
