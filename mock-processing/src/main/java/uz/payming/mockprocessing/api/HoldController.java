package uz.payming.mockprocessing.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uz.payming.mockprocessing.api.generated.HoldsApi;
import uz.payming.mockprocessing.api.generated.model.HoldDto;
import uz.payming.mockprocessing.api.generated.model.HoldRequestDto;
import uz.payming.mockprocessing.service.HoldService;

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
