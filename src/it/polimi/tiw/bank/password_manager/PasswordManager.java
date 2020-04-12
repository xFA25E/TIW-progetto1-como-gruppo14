package it.polimi.tiw.bank.password_manager;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordManager {
    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = generateAlphabet();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 30;

    private static String generateAlphabet() {
        String alphabet = "";
        for (int c = 33; c <= 126; c++) {
            alphabet += (char)c;
        }
        return alphabet;
    }

    private static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    public static String generateSalt() {
        StringBuilder salt = new StringBuilder(SALT_LENGTH);
        for (int i = 0; i < SALT_LENGTH; i++) {
            salt.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(salt);
    }

    public static String hashPassword(String password, String salt) {
        byte[] hash = hash(password.toCharArray(), salt.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String password,
                                         String hash,
                                         String salt) {
        return hashPassword(password, salt).equalsIgnoreCase(hash);
    }
}
