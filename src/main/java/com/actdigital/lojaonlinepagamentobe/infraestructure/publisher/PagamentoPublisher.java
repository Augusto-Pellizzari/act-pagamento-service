package com.actdigital.lojaonlinepagamentobe.infraestructure.publisher;

import com.actdigital.lojaonlinepagamentobe.infraestructure.config.RabbitMQConfig;
import com.actdigital.lojaonlinepagamentobe.infraestructure.event.PagamentoConfirmadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PagamentoPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publicarConfirmacao(Long pedidoId, String status) {
        String corrId = UUID.randomUUID().toString();
        CorrelationData cd = new CorrelationData(corrId);

        PagamentoConfirmadoEvent evt = new PagamentoConfirmadoEvent(
                pedidoId, status, OffsetDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAGAMENTO_CONFIRMADO_EXCHANGE,
                RabbitMQConfig.PAGAMENTO_CONFIRMADO_ROUTING_KEY,
                evt,
                message -> {
                    message.getMessageProperties().setMessageId(corrId);
                    message.getMessageProperties().setCorrelationId(corrId);
                    return message;
                },
                cd
        );

        log.debug("Publicado PagamentoConfirmadoEvent pedidoId={} status={} corrId={}",
                pedidoId, status, corrId);
    }
}