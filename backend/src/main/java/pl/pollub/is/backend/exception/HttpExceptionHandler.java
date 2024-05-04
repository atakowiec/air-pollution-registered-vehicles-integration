package pl.pollub.is.backend.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HttpExceptionHandler {
    @ExceptionHandler(HttpException.class)
    public ResponseEntity<String> handleHttpException(HttpException ex) throws JsonProcessingException {
        return ex.toResponseEntity();
    }
}
