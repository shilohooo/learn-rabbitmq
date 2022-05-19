package org.shiloh.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.shiloh.config.DelayedQueueConfig.PLUGIN_DELAYED_QUEUE_NAME;

/**
 * 延迟队列消费者 - 基于插件的延迟消息消费
 *
 * @author shiloh
 * @date 2022/5/19 21:38
 */
@Component
@Slf4j
public class DelayedQueueConsumer {

    /**
     * 监听延迟队列，接收延迟消息并消费
     *
     * @param message 消息对象
     * @author shiloh
     * @date 2022/5/19 21:40
     */
    @RabbitListener(queues = {PLUGIN_DELAYED_QUEUE_NAME})
    public void receiveDelayedMsg(Message message) {
        final String msg = new String(message.getBody(), UTF_8);
        log.info("当前时间：{}，收到延迟队列发送过来的消息：{}", new Date(), msg);
    }
}
