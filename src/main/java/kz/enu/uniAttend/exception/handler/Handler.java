package kz.enu.uniAttend.exception.handler;

import kz.enu.uniAttend.model.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class Handler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse<String>> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(
                MessageResponse.empty("Внутренняя ошибка сервера: " + ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
