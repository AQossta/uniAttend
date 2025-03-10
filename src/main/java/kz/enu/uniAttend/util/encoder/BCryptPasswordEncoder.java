package kz.enu.uniAttend.util.encoder;
import org.mindrot.jbcrypt.BCrypt;

public class BCryptPasswordEncoder implements PasswordEncoder {

    @Override
    public String hash(String password) {
        int saltRound = 12;
        String salt = BCrypt.gensalt(saltRound);
        return BCrypt.hashpw(password, salt);
    }

    @Override
    public boolean check(String password, String hashedPassword) {
        return BCrypt.checkpw(password,hashedPassword);
    }
}
