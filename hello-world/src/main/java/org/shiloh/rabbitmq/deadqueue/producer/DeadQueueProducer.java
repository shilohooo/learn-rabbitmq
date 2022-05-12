package org.shiloh.rabbitmq.deadqueue.producer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.shiloh.rabbitmq.util.RabbitMqUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import static org.shiloh.rabbitmq.constant.RabbitMqConstants.DEAD_QUEUE_NORMAL_ROUTING_KEY;
import static org.shiloh.rabbitmq.constant.RabbitMqConstants.DEAD_QUEUE_NROMAL_EXCHANGE_NAME;

/**
 * 死信队列示例 - 生产者
 * 负责发送消息给普通交换机
 *
 * @author shiloh
 * @date 2022/5/11 21:54
 */
public class DeadQueueProducer {
    public static void main(String[] args) throws IOException, TimeoutException {
        final String className = DeadQueueProducer.class.getSimpleName();
        // get channel
        final Channel channel = RabbitMqUtils.getChannel();
        // 发送延迟（死信）消息，即设置 TTL，消息存活时间
        // 设置 props
        final AMQP.BasicProperties properties = new AMQP.BasicProperties()
                .builder()
                // 设置消息过期时间，单位：ms
                .expiration("10000")
                .build();
        for (int i = 0; i < 10; i++) {
            final String msg = "info" + (i + 1);
            channel.basicPublish(DEAD_QUEUE_NROMAL_EXCHANGE_NAME, DEAD_QUEUE_NORMAL_ROUTING_KEY, null,
                    msg.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者：" + className + " 已将消息：" + msg + " 发送到 MQ 中。");
        }

    }
}
