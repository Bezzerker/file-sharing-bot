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
    public Queue textQueue(@Value("${rabbitmq.exchanges.message.queues.text.name}") String queueName) {
        return new Queue(queueName);
    }

    @Bean
    public Queue documentQueue(@Value("${rabbitmq.exchanges.message.queues.document.name}") String queueName) {
        return new Queue(queueName);
    }

    @Bean
    public Queue imageQueue(@Value("${rabbitmq.exchanges.message.queues.image.name}") String queueName) {
        return new Queue(queueName);
    }

    @Bean
    public Queue responseQueue(@Value("${rabbitmq.exchanges.message.queues.response.name}") String name) {
        return new Queue(name);
    }

    @Bean
    public Queue registrantsQueue(@Value("${rabbitmq.exchanges.user.queues.registrants.name}") String queueName,
                             @Value("${rabbitmq.exchanges.user.queues.registrants.ttl}") Long ttl,
                             @Value("${rabbitmq.exchanges.user.name}") String exchangeName,
                             @Value("${rabbitmq.exchanges.user.queues.deregister_candidates.routing_key}") String routingKey) {
        Queue registrants = new Queue(queueName);
        registrants.addArgument("x-message-ttl", ttl);
        registrants.addArgument("x-dead-letter-exchange", exchangeName);
        registrants.addArgument("x-dead-letter-routing-key", routingKey);
        return registrants;
    }

    @Bean
    public Queue dumpCandidatesQueue(@Value("${rabbitmq.exchanges.user.queues.deregister_candidates.name}") String name) {
        return new Queue(name);
    }

    @Bean
    public TopicExchange messageExchange(@Value("${rabbitmq.exchanges.message.name}") String exchange) {
        return new TopicExchange(exchange);
    }

    @Bean
    public TopicExchange userExchange(@Value("${rabbitmq.exchanges.user.name}") String exchange) {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding textBinding(@Value("${rabbitmq.exchanges.message.queues.text.routing_key}") String routingKey,
                               Queue textQueue,
                               TopicExchange messageExchange) {
        return BindingBuilder
                .bind(textQueue)
                .to(messageExchange)
                .with(routingKey);
    }

    @Bean
    public Binding documentBinding(@Value("${rabbitmq.exchanges.message.queues.document.routing_key}") String routingKey,
                                   Queue documentQueue,
                                   TopicExchange messageExchange) {
        return BindingBuilder
                .bind(documentQueue)
                .to(messageExchange)
                .with(routingKey);
    }

    @Bean
    public Binding imageBinding(@Value("${rabbitmq.exchanges.message.queues.image.routing_key}") String routingKey,
                                Queue imageQueue,
                                TopicExchange messageExchange) {
        return BindingBuilder
                .bind(imageQueue)
                .to(messageExchange)
                .with(routingKey);
    }

    @Bean
    public Binding responseBinding(@Value("${rabbitmq.exchanges.message.queues.response.routing_key}") String routingKey,
                                   Queue responseQueue,
                                   TopicExchange messageExchange) {
        return BindingBuilder
                .bind(responseQueue)
                .to(messageExchange)
                .with(routingKey);
    }

    @Bean
    public Binding registrantsBinding(@Value("${rabbitmq.exchanges.user.queues.registrants.routing_key}") String routingKey,
                                   Queue registrantsQueue,
                                   TopicExchange userExchange) {
        return BindingBuilder
                .bind(registrantsQueue)
                .to(userExchange)
                .with(routingKey);
    }

    @Bean
    public Binding dumpCandidatesBinding(@Value("${rabbitmq.exchanges.user.queues.deregister_candidates.routing_key}") String routingKey,
                                   Queue dumpCandidatesQueue,
                                   TopicExchange userExchange) {
        return BindingBuilder
                .bind(dumpCandidatesQueue)
                .to(userExchange)
                .with(routingKey);
    }
}