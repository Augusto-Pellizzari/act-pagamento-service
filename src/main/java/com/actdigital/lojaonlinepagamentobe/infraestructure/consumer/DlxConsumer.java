package com.actdigital.lojaonlinepagamentobe.infraestructure.consumer;

import com.actdigital.lojaonlinepagamentobe.infraestructure.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class DlxConsumer {

    @RabbitListener(queues = RabbitMQConfig.DLX_QUEUE)
    public void onDlx(Message message, Channel channel) throws Exception {

        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        log.error("DLX recebido: body={} headers={}", body, message.getMessageProperties());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
