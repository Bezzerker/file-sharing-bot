package ru.zerrbild.utils.ciphering;

import lombok.extern.slf4j.Slf4j;
import ru.zerrbild.utils.ciphering.enums.Algorithm;
import ru.zerrbild.utils.ciphering.exceptions.IncorrectKeyException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class Encoder {
    private final String algorithm;
    private final KeyGenerator keyGenerator;

    public Encoder(Algorithm algorithm) {
        this.algorithm = algorithm.toString();
        try {
            keyGenerator = KeyGenerator.getInstance(algorithm.toString());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBase64Key() {
        var secretKey = keyGenerator.generateKey();
        byte[] secretKeyBytes = secretKey.getEncoded();
        return DatatypeConverter.printBase64Binary(secretKeyBytes);
    }

    public String encode(Long id, String base64Key) {
        return encode(id.toString(), base64Key);
    }

    public String encode(String plainText, String base64Key) {
        try {
            var cipher = Cipher.getInstance(algorithm);
            var secretKey = base64KeyToSecretKey(base64Key);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] messageBytes = plainText.getBytes();
            byte[] ciphertext = cipher.doFinal(messageBytes);
            return DatatypeConverter.printBase64Binary(ciphertext);
        } catch (InvalidKeyException | IllegalArgumentException exception) {
            log.error("Plain text - {} | Key - {} | {}", plainText, base64Key, exception.getMessage());
            throw new IncorrectKeyException(exception);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException exception) {
            log.error("Unsupported encryption algorithm. {}", exception.getMessage());
            throw new RuntimeException(exception);
        } catch (IllegalBlockSizeException | BadPaddingException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    public String encodeForUrl(String urlParam, String base64Key) {
        var ciphertext = encode(urlParam, base64Key);
        return URLEncoder.encode(ciphertext, StandardCharsets.UTF_8);
    }

    public String encodeForUrl(Long urlParam, String base64Key) {
        var ciphertext = encode(urlParam, base64Key);
        return URLEncoder.encode(ciphertext, StandardCharsets.UTF_8);
    }

    private SecretKey base64KeyToSecretKey(String base64Key) {
        byte[] byteKey = DatatypeConverter.parseBase64Binary(base64Key);
        return new SecretKeySpec(byteKey, algorithm);
    }
}
