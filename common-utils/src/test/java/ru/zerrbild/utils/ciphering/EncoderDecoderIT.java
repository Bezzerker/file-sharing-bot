package ru.zerrbild.utils.ciphering;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.zerrbild.utils.ciphering.enums.Algorithm;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Integration Tests for Encoder and Decoder")
class EncoderDecoderIT {
    @ParameterizedTest(name = "Encoding and decoding using algorithm - {arguments}")
    @EnumSource(Algorithm.class)
    @DisplayName("Given a long number, when encoding and decoding with base64 key, then return the same number")
    void encodeAndDecode_shouldReturnSameLongNumber_whenUsingBase64KeyAndAlgorithm(Algorithm algorithm) {
        Encoder encoder = new Encoder(algorithm);
        Decoder decoder = new Decoder(algorithm);
        Long originalNumber = Long.MAX_VALUE;
        String base64Key = encoder.getBase64Key();

        String ciphertext = encoder.encode(originalNumber, base64Key);
        Long decodedNumber = decoder.decodeToLong(ciphertext, base64Key);

        assertThat(decodedNumber).isEqualTo(originalNumber);
    }

    @ParameterizedTest(name = "Encoding and decoding using algorithm - {arguments}")
    @EnumSource(Algorithm.class)
    @DisplayName("Given a plain text, when encoding and decoding with base64 key, then return the same text")
    void encodeAndDecode_shouldReturnSamePlainText_whenUsingBase64KeyAndAlgorithm(Algorithm algorithm) {
        Encoder encoder = new Encoder(algorithm);
        Decoder decoder = new Decoder(algorithm);
        String originalText = "Hello, World!";
        String base64Key = encoder.getBase64Key();

        String ciphertext = encoder.encode(originalText, base64Key);
        String decodedText = decoder.decodeToString(ciphertext, base64Key);

        assertThat(decodedText).isEqualTo(originalText);
    }
}
