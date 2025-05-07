package com.actdigital.lojaonlinepagamentobe.infraestructure.consumer;

import com.actdigital.lojaonlinepagamentobe.ports.in.PagamentoService;
import com.actdigital.lojaonlinepagamentobe.infraestructure.config.RabbitMQConfig;
import com.actdigital.lojaonlinepagamentobe.infraestructure.event.PedidoCriadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoCriadoConsumer {

    private final PagamentoService pagamentoService;

    @RabbitListener(
            queues = RabbitMQConfig.PEDIDO_CRIADO_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onPedidoCriado(
            PedidoCriadoEvent event,
            @Header(AmqpHeaders.CORRELATION_ID) String correlationId
    ) {
        pagamentoService.processarPagamento(event, correlationId);
    }
}