package tassproject.prescriptionservice;

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
    public Exchange prescriptionExchange() {
        return ExchangeBuilder
                .topicExchange("prescription.events")
                .durable(true)
                .build();
    }

    @Bean
    public Queue prescriptionQueue() {
        return QueueBuilder
                .durable("prescription.queue")
                .build();
    }

    @Bean
    public Binding bindPrescriptionEvents(Queue prescriptionQueue, Exchange prescriptionExchange) {
        return BindingBuilder
                .bind(prescriptionQueue)
                .to(prescriptionExchange)
                .with("#")
                .noargs();
    }
}
