package ru.zerrbild.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.zerrbild.utils.ciphering.Encoder;
import ru.zerrbild.utils.ciphering.enums.Algorithm;

@Configuration
public class MessageHandlerConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Encoder encoder() {
        Algorithm algorithm = Algorithm.AES;
        return new Encoder(algorithm);
    }
}