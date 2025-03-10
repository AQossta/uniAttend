package kz.enu.uniAttend.exception.handler;

import kz.enu.uniAttend.model.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class Handler {

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageResponse<String>> handleGeneralException(RuntimeException ex) {
        logger.error("Ошибка сервера: ", ex);  // Логирование ошибки с трассировкой стека
        MessageResponse<String> response = MessageResponse.empty("Ошибка сервера: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
