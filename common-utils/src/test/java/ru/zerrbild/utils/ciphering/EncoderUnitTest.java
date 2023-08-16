package ru.zerrbild.utils.ciphering;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.zerrbild.utils.ciphering.enums.Algorithm;
import ru.zerrbild.utils.ciphering.exceptions.IncorrectKeyException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Unit Test for Encoder")
class EncoderUnitTest {
    private static final String KEY = "UWXrwEU1ZrnA4DQZbp5WHA==";
    @ParameterizedTest(name = "Encoding using algorithm - {arguments}")
    @EnumSource(Algorithm.class)
    @DisplayName("Given plain text and key, when encode plain text using base64 key, then return ciphertext")
    void encode_shouldEncodePlainTextUsingBase64Key_ifKeyIsCorrect(Algorithm algorithm) {
        Encoder encoder = new Encoder(algorithm);
        String plainText = "Plain Text";
        String correctCiphertext = switch (algorithm) {
            case AES -> "27Tu7MFMZPw2YP8u/4De9A==";
            case RC2 -> "0rxCAMqFyfUd1EJ7bUfe7w==";
            case ARCFOUR -> "4vg1W13KWU1YDg==";
            case BLOWFISH -> "4EPRPGHu+R/pqtLI/lLCjQ==";
            default -> throw new IllegalArgumentException("Unknown encryption algorithm");
        };

        String ciphertext = encoder.encode(plainText, KEY);

        assertThat(ciphertext).isEqualTo(correctCiphertext);
    }

    @ParameterizedTest
    @EnumSource(Algorithm.class)
    @DisplayName("Given plain text as max long number and key, when encode plain text using base64 key, then return ciphertext")
    void encode_shouldEncodePlainTextUsingBase64Key_ifPlainTextIsConvertibleToLong(Algorithm algorithm) {
        Encoder encoder = new Encoder(algorithm);
        Long longPlainText = Long.MAX_VALUE;
        String correctCiphertext = switch (algorithm) {
            case AES -> "fX7GzssfURBUgQzRTHZLXH6BTCATKuu+yHyLXgvXuqs=";
            case RC2 -> "i1VwFgXaL5s4hkh/uMjVTMK74EkjgmiL";
            case ARCFOUR -> "i6ZmAQDdPxgTTFFOVYzPtiLmkQ==";
            case BLOWFISH -> "CGItayG02iSY20rAiUeHWI5IVG2soMDd";
            default -> throw new IllegalArgumentException("Unknown encryption algorithm");
        };

        String ciphertext = encoder.encode(longPlainText, KEY);

        assertThat(ciphertext).isEqualTo(correctCiphertext);
    }

    @Test
    @DisplayName("Given plain text and incorrect key, when encode plain text using base64 key, then throw IncorrectKeyException")
    void encode_shouldThrowIncorrectKeyException_ifKeyIsIncorrect() {
        Algorithm defaultAlgorithm = Algorithm.AES;
        Encoder encoder = new Encoder(defaultAlgorithm);
        String plainText = "Plain Text";

        assertAll(
                // InvalidKey
                () -> {
                    String incorrectKey = "invalid";
                    assertThrows(
                            IncorrectKeyException.class,
                            () -> encoder.encode(plainText, incorrectKey)
                    );
                },
                // IllegalArgument
                () -> {
                    String incorrectKey = "";
                    assertThrows(
                            IncorrectKeyException.class,
                            () -> encoder.encode(plainText, incorrectKey)
                    );
                }
        );
    }
}
