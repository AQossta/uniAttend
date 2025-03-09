package kz.enu.uniAttend.exception;

public class SessionHasExpiredException extends RuntimeException {
    public SessionHasExpiredException() {
        super ("Session has expired");
    }
}
