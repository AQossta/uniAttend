package kz.enu.uniAttend.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Invalid password");
    }
}
