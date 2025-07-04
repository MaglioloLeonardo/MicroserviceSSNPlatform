package tassproject.notificationservice;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Exchange notificationExchange() {
        return ExchangeBuilder
                .directExchange("notification.events")
                .durable(true)
                .build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder
                .durable("notification.queue")
                .build();
    }

    @Bean
    public Binding bindNotification(Queue notificationQueue, Exchange notificationExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(notificationExchange)
                .with("INFO")
                .noargs();
    }
}
