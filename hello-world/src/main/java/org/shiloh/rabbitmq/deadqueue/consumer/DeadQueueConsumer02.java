package org.shiloh.rabbitmq.deadqueue.consumer;

import com.rabbitmq.client.Channel;
import org.shiloh.rabbitmq.util.RabbitMqUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.shiloh.rabbitmq.constant.RabbitMqConstants.DEAD_QUEUE_DEAD_QUEUE_NAME;

/**
 * 死信队列示例 - 消费者01
 *
 * @author shiloh
 * @date 2022/5/11 21:28
 */
public class DeadQueueConsumer02 {
    public static void main(String[] args) throws IOException, TimeoutException {
        final String className = DeadQueueConsumer02.class.getSimpleName();
        // get channel1
        final Channel channel = RabbitMqUtils.getChannel();

        System.out.println("消费者：" + className + " 等待接收消息...");

        // 接收消息并消费
        channel.basicConsume(
                DEAD_QUEUE_DEAD_QUEUE_NAME,
                true,
                (consumerTag, msg) -> {
                    System.out.println("消费者：" + className + "接收到消息：" +
                            new String(msg.getBody(), UTF_8));
                },
                consumerTag -> {
                }
        );
    }
}
