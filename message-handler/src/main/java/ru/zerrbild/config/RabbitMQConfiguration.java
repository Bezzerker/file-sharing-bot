package ru.zerrbild.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue responseQueue(@Value("${rabbitmq.queue.response}") String name) {
        return new Queue(name);
    }

    @Bean
    public TopicExchange messageExchange(@Value("${rabbitmq.exchange.message.name}") String exchange) {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding responseBinding(@Value("${rabbitmq.exchange.message.routing_key.to_response_queue}") String routingKey,
                                   Queue responseQueue,
                                   TopicExchange messageExchange) {
        return BindingBuilder
                .bind(responseQueue)
                .to(messageExchange)
                .with(routingKey);
    }
}