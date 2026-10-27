package uz.fintech.cardprocessing.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uz.fintech.cardprocessing.api.generated.model.ErrorDto;
import uz.fintech.cardprocessing.service.ProcessingException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ProcessingException.class)
    public ResponseEntity<ErrorDto> processing(ProcessingException e) {
        return ResponseEntity.status(e.code().status())
                .body(new ErrorDto()
                        .code(e.code().name())
                        .message(e.getMessage())
                        .retryable(e.code().retryable()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> validation(MethodArgumentNotValidException e) {
        String field = e.getBindingResult().getFieldErrors().isEmpty()
                ? "request"
                : e.getBindingResult().getFieldErrors().get(0).getField();
        return ResponseEntity.badRequest()
                .body(new ErrorDto()
                        .code("VALIDATION_ERROR")
                        .message("Invalid field: " + field)
                        .retryable(false)
                );
    }
}
