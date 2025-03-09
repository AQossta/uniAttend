package kz.enu.uniAttend.exception;

public class SessionNotFoundException extends RuntimeException{
    public SessionNotFoundException() {
        super("Session not found");
    }
}
