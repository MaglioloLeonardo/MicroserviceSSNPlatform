package tassproject.dispensationservice;

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
    public Exchange dispensationExchange() {
        return ExchangeBuilder
                .topicExchange("dispensation.events")
                .durable(true)
                .build();
    }

    @Bean
    public Queue dispensationQueue() {
        return QueueBuilder
                .durable("dispensation.queue")
                .build();
    }

    @Bean
    public Binding bindDispensationEvents(Queue dispensationQueue, Exchange dispensationExchange) {
        return BindingBuilder
                .bind(dispensationQueue)
                .to(dispensationExchange)
                .with("#")
                .noargs();
    }
}
