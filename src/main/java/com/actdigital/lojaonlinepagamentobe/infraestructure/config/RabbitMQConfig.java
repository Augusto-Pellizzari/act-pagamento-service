package com.actdigital.lojaonlinepagamentobe.infraestructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;

@Configuration
public class RabbitMQConfig {

    // Exchanges e filas de pedidos
    public static final String PEDIDO_CRIADO_EXCHANGE    = "pedido.criado.exchange";
    public static final String PEDIDO_CRIADO_QUEUE       = "pedido.criado.queue";
    public static final String PEDIDO_CRIADO_ROUTING_KEY = "pedido.criado";

    // Exchanges e filas de confirmação de pagamento
    public static final String PAGAMENTO_CONFIRMADO_EXCHANGE    = "pagamento.confirmado.exchange";
    public static final String PAGAMENTO_CONFIRMADO_QUEUE       = "pagamento.confirmado.queue";
    public static final String PAGAMENTO_CONFIRMADO_ROUTING_KEY = "pagamento.confirmado";

    // Dead-letter exchange/queue
    public static final String DLX_EXCHANGE    = "dlx.exchange";
    public static final String DLX_QUEUE       = "dlx.queue";
    public static final String DLX_ROUTING_KEY = "dlx.routing-key";

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf,
                                         Jackson2JsonMessageConverter conv) {
        RabbitTemplate rt = new RabbitTemplate(cf);
        rt.setMessageConverter(conv);
        rt.setChannelTransacted(true);
        return rt;
    }

    @Bean
    public DirectExchange pedidoCriadoExchange() {
        return ExchangeBuilder
                .directExchange(PEDIDO_CRIADO_EXCHANGE)
                .durable(true)
                .withArgument("alternate-exchange", DLX_EXCHANGE)
                .build();
    }

    @Bean
    public Queue pedidoCriadoQueue() {
        return QueueBuilder
                .durable(PEDIDO_CRIADO_QUEUE)
                .deadLetterExchange(DLX_EXCHANGE)
                .deadLetterRoutingKey(DLX_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding pedidoCriadoBinding() {
        return BindingBuilder
                .bind(pedidoCriadoQueue())
                .to(pedidoCriadoExchange())
                .with(PEDIDO_CRIADO_ROUTING_KEY);
    }

    @Bean
    public DirectExchange pagamentoConfirmadoExchange() {
        return ExchangeBuilder
                .directExchange(PAGAMENTO_CONFIRMADO_EXCHANGE)
                .durable(true)
                .withArgument("alternate-exchange", DLX_EXCHANGE)
                .build();
    }

    @Bean
    public Queue pagamentoConfirmadoQueue() {
        return QueueBuilder
                .durable(PAGAMENTO_CONFIRMADO_QUEUE)
                .deadLetterExchange(DLX_EXCHANGE)
                .deadLetterRoutingKey(DLX_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding pagamentoConfirmadoBinding() {
        return BindingBuilder
                .bind(pagamentoConfirmadoQueue())
                .to(pagamentoConfirmadoExchange())
                .with(PAGAMENTO_CONFIRMADO_ROUTING_KEY);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return ExchangeBuilder.directExchange(DLX_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(DLX_QUEUE).build();
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder
                .bind(dlxQueue())
                .to(dlxExchange())
                .with(DLX_ROUTING_KEY);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf,
            Jackson2JsonMessageConverter conv
    ) {
        var f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(conv);
        f.setConcurrentConsumers(3);
        f.setPrefetchCount(5);
        RetryOperationsInterceptor retry = RetryInterceptorBuilder.stateless()
                .maxAttempts(5)
                .backOffOptions(1000, 2.0, 10000)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
        f.setAdviceChain(retry);
        return f;
    }
}