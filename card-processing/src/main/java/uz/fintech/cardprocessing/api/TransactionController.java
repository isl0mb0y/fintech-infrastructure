package uz.fintech.cardprocessing.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uz.fintech.cardprocessing.api.generated.TransactionsApi;
import uz.fintech.cardprocessing.api.generated.model.RegistryDto;
import uz.fintech.cardprocessing.api.generated.model.TransactionDto;
import uz.fintech.cardprocessing.service.TransactionService;

import java.time.LocalDate;
import java.util.UUID;

@RestController
public class TransactionController implements TransactionsApi {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public ResponseEntity<RegistryDto> registryGet(LocalDate date) {
        return ResponseEntity.ok(ApiMapper.toDto(date, transactionService.registry(date)));
    }

    @Override
    public ResponseEntity<TransactionDto> transactionInquiry(UUID requestId) {
        return ResponseEntity.ok(ApiMapper.toDto(transactionService.inquiry(requestId)));
    }
}
