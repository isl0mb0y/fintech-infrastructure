package uz.payming.cardprocessing.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uz.payming.cardprocessing.api.generated.CreditsApi;
import uz.payming.cardprocessing.api.generated.model.CreditDto;
import uz.payming.cardprocessing.api.generated.model.CreditRequestDto;
import uz.payming.cardprocessing.service.CreditService;

@RestController
public class CreditController implements CreditsApi {
    private final CreditService creditService;

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @Override
    public ResponseEntity<CreditDto> creditCreate(CreditRequestDto creditRequestDto) {
        return ResponseEntity.ok(ApiMapper.toDto(
                creditService.create(creditRequestDto.getRequestId(), creditRequestDto.getCardToken(), creditRequestDto.getAmount())
        ));
    }
}
