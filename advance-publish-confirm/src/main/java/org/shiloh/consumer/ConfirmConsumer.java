package org.shiloh.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.shiloh.config.AdvancePublishConfirmConfig.CONFIRM_QUEUE_NAME;

/**
 * 发布确认高级 - 消费者
 *
 * @author shiloh
 * @date 2022/5/23 21:45
 */
@Component
@Slf4j
public class ConfirmConsumer {

    /**
     * 监听指定队列并接收消息，然后消费
     *
     * @param message 消息对象
     * @author shiloh
     * @date 2022/5/23 21:46
     */
    @RabbitListener(queues = {CONFIRM_QUEUE_NAME})
    public void receiveMsg(Message message) {
        final String msg = new String(message.getBody(), UTF_8);
        log.info("从队列：{} 中接收到消息：'{}'", CONFIRM_QUEUE_NAME, msg);
    }
}
