package kz.enu.uniAttend.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("Пользователь уже существует");
    }
}
