package ru.zerrbild.utils.ciphering;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zerrbild.utils.ciphering.enums.Algorithm;
import ru.zerrbild.utils.ciphering.exceptions.IncorrectCiphertextException;
import ru.zerrbild.utils.ciphering.exceptions.IncorrectKeyException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@DisplayName("Unit Test for Decoder")
@ExtendWith(MockitoExtension.class)
class DecoderUnitTest {
    @Mock
    private Encoder encoder;
    private static final String KEY = "wi0vxYpuBFFj6Z2iJLqFWQ==";
    private static final Algorithm defaultAlgorithm = Algorithm.AES;

    @ParameterizedTest(name = "Decoding using algorithm - {arguments}")
    @EnumSource(Algorithm.class)
    @DisplayName("Given a ciphertext and a key, when decode to string, then return the original plaintext as a string")
    void decodeToString_shouldDecodeCiphertextToString_ifCiphertextAndKeyAreIntact(Algorithm algorithm) {
        String plainText = "Plain Text";
        when(encoder.encode(plainText, KEY)).thenAnswer(invocation -> switch (algorithm) {
            case AES -> "UWXrwEU1ZrnA4DQZbp5WHA==";
            case ARCFOUR -> "itydGuV7MvP+Hw==";
            case RC2 -> "UCotDF2FQkAdAyX79NQFYQ==";
            case BLOWFISH -> "6vaFgwNGLe1eaSxQgbiGkw==";
            default -> throw new IllegalArgumentException("Unsupported encryption algorithm");
        });
        String cipherText = encoder.encode(plainText, KEY);
        Decoder decoder = new Decoder(algorithm);

        String decodedText = decoder.decodeToString(cipherText, KEY);

        assertThat(decodedText).isEqualTo(plainText);
    }

    @Test
    @DisplayName("Given a ciphertext and a incorrect key, when decode to string, then throw IncorrectKeyException")
    void decodeToString_shouldThrowIncorrectKeyException_ifKeyIsIncorrect() {
        String plainText = "Plain Text";
        doReturn("UWXrwEU1ZrnA4DQZbp5WHA==").when(encoder).encode(plainText, KEY);
        String ciphertext = encoder.encode(plainText, KEY);
        Decoder decoder = new Decoder(defaultAlgorithm);

        assertAll(
                // InvalidKey
                () -> {
                    String incorrectKey = "invalid";
                    assertThrows(
                            IncorrectKeyException.class,
                            () -> decoder.decodeToString(ciphertext, incorrectKey)
                    );
                },
                // BadPadding
                () -> {
                    String incorrectKey = "1rQTfI57LfbUhd32GnWfTQ==";
                    assertThrows(
                            IncorrectKeyException.class,
                            () -> decoder.decodeToString(ciphertext, incorrectKey)
                    );
                },
                // IllegalArgument
                () -> {
                    String incorrectKey = "";
                    assertThrows(
                            IncorrectKeyException.class,
                            () -> decoder.decodeToString(ciphertext, incorrectKey)
                    );
                }
        );
    }

    @Test
    @DisplayName("Given a incorrect ciphertext and a key, when decode to string, then throw IncorrectCiphertextException")
    void decodeToString_shouldThrowIncorrectCiphertextException_ifCiphertextIsIncorrect() {
        Decoder decoder = new Decoder(defaultAlgorithm);
        String incorrectCiphertext = "damage";

        assertThrows(
                IncorrectCiphertextException.class,
                () -> decoder.decodeToString(incorrectCiphertext, KEY)
        );
    }

    @ParameterizedTest(name = "Decoding using algorithm - {arguments}")
    @EnumSource(Algorithm.class)
    @DisplayName("Given a ciphertext and a key, when decode to long, then return the original plaintext as a long")
    void decodeToLong_shouldDecodeCiphertextToLong_ifCiphertextIsConvertibleToLong(Algorithm algorithm) {
        Long longPlainText = 1337L;
        when(encoder.encode(longPlainText, KEY)).thenAnswer(invocation -> switch (algorithm) {
            case AES -> "J9HhYVzoJ/UBByPwqpkrEg==";
            case ARCFOUR -> "64PPRA==";
            case RC2 -> "hMsLXKnVFRs=";
            case BLOWFISH -> "cJmH9k/j8sE=";
            default -> throw new IllegalArgumentException("Unsupported encryption algorithm");
        });
        String cipherText = encoder.encode(longPlainText, KEY);
        Decoder decoder = new Decoder(algorithm);

        Long decodedText = decoder.decodeToLong(cipherText, KEY);

        assertThat(decodedText).isEqualTo(longPlainText);
    }

    @Test
    @DisplayName("Given a letter ciphertext and a key, when decode to long, then throw IncorrectCiphertextException")
    void decodeToLong_shouldThrowIncorrectCiphertextException_ifCiphertextIsConvertableToLong() {
        String plainText = "Plain Text";
        doReturn("UWXrwEU1ZrnA4DQZbp5WHA==").when(encoder).encode(plainText, KEY);
        String cipherText = encoder.encode(plainText, KEY);
        Decoder decoder = new Decoder(defaultAlgorithm);

        assertThrows(
                IncorrectCiphertextException.class,
                () -> decoder.decodeToLong(cipherText, KEY)
        );
    }
}
