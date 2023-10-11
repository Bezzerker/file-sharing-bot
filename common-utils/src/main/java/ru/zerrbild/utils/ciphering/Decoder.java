package ru.zerrbild.utils.ciphering;

import lombok.extern.slf4j.Slf4j;
import ru.zerrbild.utils.ciphering.enums.Algorithm;
import ru.zerrbild.utils.ciphering.exceptions.IncorrectCiphertextException;
import ru.zerrbild.utils.ciphering.exceptions.IncorrectKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class Decoder {
    private final String algorithm;

    public Decoder(Algorithm algorithm) {
        this.algorithm = algorithm.toString();
    }

    public String decodeToString(String ciphertext, String base64Key) {
        try {
            byte[] keyBytes = DatatypeConverter.parseBase64Binary(base64Key);
            var secretKey = new SecretKeySpec(keyBytes, algorithm);
            var cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] ciphertextBytes = DatatypeConverter.parseBase64Binary(ciphertext);
            byte[] decryptedMessage = cipher.doFinal(ciphertextBytes);
            return new String(decryptedMessage);
        } catch (InvalidKeyException | BadPaddingException | IllegalArgumentException exception) {
            log.error("Ciphertext - {} | Key - {} | {}", ciphertext, base64Key, exception.getMessage());
            throw new IncorrectKeyException(exception);
        } catch (IllegalBlockSizeException | ArrayIndexOutOfBoundsException exception) {
            log.error("Ciphertext - {} | Key - {} | {}", ciphertext, base64Key, exception.getMessage());
            throw new IncorrectCiphertextException(exception);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException exception) {
            log.error("Unsupported encryption algorithm. {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    public Long decodeToLong(String ciphertext, String base64Key) {
        try {
            var text = decodeToString(ciphertext, base64Key);
            return Long.valueOf(text);
        } catch (NumberFormatException exception) {
            log.error("Failed to convert string to number. {}", exception.getMessage());
            throw new IncorrectCiphertextException(exception);
        }
    }
}
