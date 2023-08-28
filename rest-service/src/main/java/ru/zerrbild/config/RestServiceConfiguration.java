package ru.zerrbild.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.zerrbild.utils.ciphering.Decoder;
import ru.zerrbild.utils.ciphering.enums.Algorithm;

@Configuration
public class RestServiceConfiguration {
    @Bean
    public Decoder decoder() {
        Algorithm algorithm = Algorithm.AES;
        return new Decoder(algorithm);
    }
}