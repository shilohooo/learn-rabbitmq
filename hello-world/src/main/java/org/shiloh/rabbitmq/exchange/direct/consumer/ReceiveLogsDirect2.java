package org.shiloh.rabbitmq.exchange.direct.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.shiloh.rabbitmq.util.RabbitMqUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.rabbitmq.client.BuiltinExchangeType.DIRECT;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.shiloh.rabbitmq.constant.RabbitMqConstants.DIRECT_EXCHANGE_NAME;

/**
 * 直接交换机 - 消费者02
 *
 * @author shiloh
 * @date 2022/5/6 20:49
 */
public class ReceiveLogsDirect2 {
    /**
     * 队列名称
     */
    private static final String QUEUE_NAME = "disk";

    /**
     * routing key
     */
    private static final String ROUTING_KEY = "error";


    public static void main(String[] args) {
        // get channel
        try {
            final Channel channel = RabbitMqUtils.getChannel();
            // 声明一个直接交换机
            channel.exchangeDeclare(DIRECT_EXCHANGE_NAME, DIRECT);
            // 声明一个队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            // 使用 routing key 将队列绑定到交换机
            channel.queueBind(QUEUE_NAME, DIRECT_EXCHANGE_NAME, ROUTING_KEY);

            // 开始接收消息并消费
            final DeliverCallback deliverCallback = (tag, msg) -> {
                final String className = ReceiveLogsDirect2.class.getSimpleName();
                System.out.println(className + " 接收到的消息为：" + new String(msg.getBody(), UTF_8));
            };
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, tag -> {
            });
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
