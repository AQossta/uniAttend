package kz.enu.uniAttend.exception;

public class AuthenticationErrorException extends RuntimeException {
    public AuthenticationErrorException() {
        super("Authentication error");
    }
}
