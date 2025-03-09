package kz.enu.uniAttend.util.encoder;

public interface PasswordEncoder {
    public String hash(String password);

    public boolean check(String password, String hash);
}
