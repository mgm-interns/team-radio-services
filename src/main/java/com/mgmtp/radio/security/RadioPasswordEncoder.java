package com.mgmtp.radio.security;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

public class RadioPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence password) {
        return BCrypt.hashpw(password.toString(), BCrypt.gensalt(8));
    }

    @Override
    public boolean matches(CharSequence password, String encodeString) {
        return BCrypt.checkpw(password.toString(), encodeString);
    }
}
