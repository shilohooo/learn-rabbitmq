package org.shiloh.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.shiloh.config.AdvancePublishConfirmConfig.WARNING_QUEUE_NAME;

/**
 * 报警队列消费者
 *
 * @author shiloh
 * @date 2022/5/29 16:19
 */
@Component
@Slf4j
public class WarningConsumer {
    /**
     * 接收报警队列的消息并进行消费
     *
     * @author shiloh
     * @date 2022/5/29 16:21
     */
    @RabbitListener(queues = {WARNING_QUEUE_NAME})
    public void receiveWarningMsg(Message message) {
        final String msg = new String(message.getBody(), UTF_8);
        log.error("报警发现不可路由的消息：{}", msg);
    }
}
