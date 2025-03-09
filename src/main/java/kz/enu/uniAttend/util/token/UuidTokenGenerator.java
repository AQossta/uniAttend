package kz.enu.uniAttend.util.token;

import java.util.UUID;

public class UuidTokenGenerator implements TokenGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
