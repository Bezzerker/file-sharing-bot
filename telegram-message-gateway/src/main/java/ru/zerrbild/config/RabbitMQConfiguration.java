package ru.zerrbild.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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
    public Queue textQueue(@Value("${rabbitmq.queue.text}") String queueName) {
        return new Queue(queueName);
    }

    @Bean
    public Queue documentQueue(@Value("${rabbitmq.queue.document}") String queueName) {
        return new Queue(queueName);
    }

    @Bean
    public Queue imageQueue(@Value("${rabbitmq.queue.image}") String queueName) {
        return new Queue(queueName);
    }

    @Bean
    public TopicExchange messageExchange(@Value("${rabbitmq.exchange.name}") String exchange) {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding textBinding(@Value("${rabbitmq.exchange.routing_key.to_text_queue}") String routingKey,
                               Queue textQueue,
                               TopicExchange messageExchange) {
        return BindingBuilder
                .bind(textQueue)
                .to(messageExchange)
                .with(routingKey);
    }

    @Bean
    public Binding documentBinding(@Value("${rabbitmq.exchange.routing_key.to_document_queue}") String routingKey,
                                   Queue documentQueue,
                                   TopicExchange messageExchange) {
        return BindingBuilder
                .bind(documentQueue)
                .to(messageExchange)
                .with(routingKey);
    }

    @Bean
    public Binding imageBinding(@Value("${rabbitmq.exchange.routing_key.to_image_queue}") String routingKey,
                                Queue imageQueue,
                                TopicExchange messageExchange) {
        return BindingBuilder
                .bind(imageQueue)
                .to(messageExchange)
                .with(routingKey);
    }
}
