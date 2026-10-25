package uz.payming.cardprocessing.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uz.payming.cardprocessing.api.generated.HoldsApi;
import uz.payming.cardprocessing.api.generated.model.HoldDto;
import uz.payming.cardprocessing.api.generated.model.HoldRequestDto;
import uz.payming.cardprocessing.service.HoldService;

import java.util.UUID;

@RestController
public class HoldController implements HoldsApi {

    private final HoldService holdService;

    public HoldController(HoldService holdService) {
        this.holdService = holdService;
    }


    @Override
    public ResponseEntity<HoldDto> holdConfirm(UUID id) {
        return ResponseEntity.ok(ApiMapper.toDto(holdService.confirm(id)));
    }

    @Override
    public ResponseEntity<HoldDto> holdCreate(HoldRequestDto holdRequestDto) {
        return ResponseEntity.ok(ApiMapper.toDto(
                holdService.create(holdRequestDto.getRequestId(), holdRequestDto.getCardToken(), holdRequestDto.getAmount())
        ));
    }

    @Override
    public ResponseEntity<HoldDto> holdReverse(UUID id) {
        return ResponseEntity.ok(ApiMapper.toDto(holdService.reverse(id)));
    }
}
