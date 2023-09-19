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
    public Queue registrantsQueue(@Value("${rabbitmq.queue.registrants.name}") String queueName,
                             @Value("${rabbitmq.queue.registrants.ttl}") Long ttl,
                             @Value("${rabbitmq.exchange.user.name}") String exchangeName,
                             @Value("${rabbitmq.exchange.user.routing_key.to_deregister_candidates_queue}") String routingKey) {
        Queue registrants = new Queue(queueName);
        registrants.addArgument("x-message-ttl", ttl);
        registrants.addArgument("x-dead-letter-exchange", exchangeName);
        registrants.addArgument("x-dead-letter-routing-key", routingKey);
        return registrants;
    }

    @Bean
    public Queue dumpCandidatesQueue(@Value("${rabbitmq.queue.deregister_candidates}") String name) {
        return new Queue(name);
    }

    @Bean
    public TopicExchange messageExchange(@Value("${rabbitmq.exchange.message.name}") String exchange) {
        return new TopicExchange(exchange);
    }

    @Bean
    public TopicExchange userExchange(@Value("${rabbitmq.exchange.user.name}") String exchange) {
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

    @Bean
    public Binding registrantsBinding(@Value("${rabbitmq.exchange.user.routing_key.to_registrants_queue}") String routingKey,
                                   Queue registrantsQueue,
                                   TopicExchange userExchange) {
        return BindingBuilder
                .bind(registrantsQueue)
                .to(userExchange)
                .with(routingKey);
    }

    @Bean
    public Binding dumpCandidatesBinding(@Value("${rabbitmq.exchange.user.routing_key.to_deregister_candidates_queue}") String routingKey,
                                   Queue dumpCandidatesQueue,
                                   TopicExchange userExchange) {
        return BindingBuilder
                .bind(dumpCandidatesQueue)
                .to(userExchange)
                .with(routingKey);
    }
}