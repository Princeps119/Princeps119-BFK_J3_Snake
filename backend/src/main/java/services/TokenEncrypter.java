package services;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;
import java.util.logging.Logger;

//decryption needed, as instead I would need to iterate through all documents and hash that mail and compare against the hashed tokenmail
public class TokenEncrypter {

    public static final Logger logger = Logger.getLogger(TokenEncrypter.class.getName());
    public static  Key key;

    static {
        String secret = System.getenv("ENCRYPTION_KEY");

        if (null == secret) {
            logger.warning("Docker ENCRYPTION_KEY environment variable has not been set");
            try {
                InputStream input = TokenEncrypter.class.getClassLoader().getResourceAsStream("config.properties");
                Properties props = new Properties();
                props.load(input);
                secret = props.getProperty("encryption.key");
                if (secret == null || secret.length() < 32) {
                    throw new IllegalStateException("Invalid encryption key");
                }
            } catch (IOException | IllegalStateException e) {
                logger.warning("Could not load encryption key");
            }
        }
            key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
    }

    public static String encrypt(final String token) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        // Encrypt
        final Cipher encryptCipher = Cipher.getInstance("AES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = encryptCipher.doFinal(token.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String token) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        // Decrypt
        Cipher decryptCipher = Cipher.getInstance("AES");
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = decryptCipher.doFinal(Base64.getDecoder().decode(token));
        return new String(decryptedBytes);
    }
}
