package org.shiloh.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 延迟队列消费者
 *
 * @author shiloh
 * @date 2022/5/16 22:02
 */
@Component
@Slf4j
public class DelayQueueConsumer {

    /**
     * 接收指定队列中的消息，
     * <p>
     * 队列名称由：{@link RabbitListener#queues()} 指定
     *
     * @param message 消息对象
     * @param channel 信道
     * @author shiloh
     * @date 2022/5/16 22:03
     */
    @RabbitListener(queues = "delay-dead-letter-queue")
    public void receiveMsg(Message message, Channel channel) {
        final String msg = new String(message.getBody(), UTF_8);
        log.info("当前时间：{}，接收到死信队列的消息：{}", new Date(), msg);
    }
}
