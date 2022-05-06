package org.shiloh.rabbitmq.exchange.topic.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.shiloh.rabbitmq.util.RabbitMqUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.rabbitmq.client.BuiltinExchangeType.TOPIC;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.shiloh.rabbitmq.constant.RabbitMqConstants.TOPIC_EXCHANGE_NAME;

/**
 * 主题交换机案例 - 消费者01
 * 绑定主题：*.orange.*
 * 主题由多个单词组成，单词之间使用英文 . 符号分隔，且还可以使用2个通配符 * 和 #
 * * 代表一个单词
 * # 代表0个或多个单词
 *
 * @author shiloh
 * @date 2022/5/6 21:25
 */
public class TopicExchangeConsumer01 {
    /**
     * 队列名称
     */
    private static final String QUEUE_NAME = "topic_queue_01";

    /**
     * routing key
     */
    public static final String ROUTING_KEY = "*.orange.*";

    public static void main(String[] args) {
        // get channel
        try {
            final Channel channel = RabbitMqUtils.getChannel();
            // 声明交换机
            channel.exchangeDeclare(TOPIC_EXCHANGE_NAME, TOPIC);
            // 声明一个队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            // 使用 routing key 绑定 队列与交换机之间的关系
            channel.queueBind(QUEUE_NAME, TOPIC_EXCHANGE_NAME, ROUTING_KEY);

            final String className = TopicExchangeConsumer01.class.getSimpleName();
            System.out.println(className + " 等待接收消息...");

            // 接收消息并消费
            final DeliverCallback deliverCallback = (tag, msg) -> {
                System.out.println(className + " 接收到消息：" + new String(msg.getBody(), UTF_8));
                final String routingKey = msg.getEnvelope().getRoutingKey();
                System.out.println("接收队列为：" + QUEUE_NAME + "，绑定Key为：" + routingKey);
            };
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, tag -> {
            });
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
