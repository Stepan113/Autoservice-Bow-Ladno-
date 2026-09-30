package org.lab.kpoproject.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordGenerator {
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*";
    private static final int minLength = 8;
    private static final int maxLength = 16;
    private static final String ALL = LOWER + UPPER + DIGITS + SPECIAL;
    private static final SecureRandom RND = new SecureRandom();

    @Autowired
    private PasswordEncoder encoder;

    public String generate() {
        final StringBuilder sb = new StringBuilder(maxLength);
        sb.append(pick(LOWER)).append(pick(UPPER))
                .append(pick(DIGITS)).append(pick(SPECIAL));
        for (int i = minLength; i < maxLength; i++) {
            sb.append(pick(ALL));
        }
        return shuffle(sb.toString());
    }

    public String encode(final String password) {
        return encoder.encode(password);
    }

    private char pick(final String s) {
        return s.charAt(RND.nextInt(s.length()));
    }

    private String shuffle(final String s) {
        final char[] a = s.toCharArray();
        for (int i = a.length - 1; i > 0; i--) {
            final int j = RND.nextInt(i + 1);
            final char t = a[i];
            a[i] = a[j];
            a[j] = t;
        }
        return new String(a);
    }
}
