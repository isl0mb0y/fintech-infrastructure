package uz.payming.cardprocessing.service;

public class ProcessingException extends RuntimeException {
    private final ErrorCode code;

    public ProcessingException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode code() {return code;}
}
