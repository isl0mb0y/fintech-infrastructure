package uz.fintech.cardprocessing.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uz.fintech.cardprocessing.api.generated.CardsApi;
import uz.fintech.cardprocessing.api.generated.model.CardDto;
import uz.fintech.cardprocessing.api.generated.model.CardNewRequestDto;
import uz.fintech.cardprocessing.api.generated.model.CardNewResponseDto;
import uz.fintech.cardprocessing.api.generated.model.CardVerifyRequestDto;
import uz.fintech.cardprocessing.service.CardService;
import uz.fintech.cardprocessing.service.PendingBinding;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
public class CardController implements CardsApi {

    private static final DateTimeFormatter EXPIRY = DateTimeFormatter.ofPattern("MM/yy");

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public ResponseEntity<CardDto> cardInfo(UUID token) {
        return ResponseEntity.ok(ApiMapper.toDto(cardService.info(token)));
    }

    @Override
    public ResponseEntity<CardNewResponseDto> cardNew(CardNewRequestDto cardNewRequestDto) {
        PendingBinding b = cardService.startBinding(
                cardNewRequestDto.getPan(),
                YearMonth.parse(cardNewRequestDto.getExpiry(), EXPIRY),
                cardNewRequestDto.getPhone()
        );

        return ResponseEntity.ok(new CardNewResponseDto()
                .tempId(b.tempId())
                .phoneMask(cardService.maskPhone(b.cardToken()))
                .otpExpiresAt(ApiMapper.at(b.expiresAt()))
        );
    }

    @Override
    public ResponseEntity<CardDto> cardVerify(CardVerifyRequestDto cardVerifyRequestDto) {
        return ResponseEntity.ok(
                ApiMapper.toDto(
                        cardService.verify(
                                cardVerifyRequestDto.getTempId(),
                                cardVerifyRequestDto.getOtp()
                        )
                )
        );
    }
}
