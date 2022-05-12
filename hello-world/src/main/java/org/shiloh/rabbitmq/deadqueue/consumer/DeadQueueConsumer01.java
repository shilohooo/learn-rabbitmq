package org.shiloh.rabbitmq.deadqueue.consumer;

import com.rabbitmq.client.Channel;
import org.shiloh.rabbitmq.util.RabbitMqUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.rabbitmq.client.BuiltinExchangeType.DIRECT;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.shiloh.rabbitmq.constant.RabbitMqConstants.*;

/**
 * 死信队列示例 - 消费者01
 *
 * @author shiloh
 * @date 2022/5/11 21:28
 */
public class DeadQueueConsumer01 {
    public static void main(String[] args) throws IOException, TimeoutException {
        final String className = DeadQueueConsumer01.class.getSimpleName();
        // get channel1
        final Channel channel = RabbitMqUtils.getChannel();
        // 声明死信和普通交换机，类型为 Direct
        channel.exchangeDeclare(DEAD_QUEUE_DEAD_EXCHANGE_NAME, DIRECT);
        channel.exchangeDeclare(DEAD_QUEUE_NROMAL_EXCHANGE_NAME, DIRECT);

        // 声明普通队列，此处需要传输特定的参数才能将消息转发到死信队列
        final Map<String, Object> paramMap = new HashMap<>(3);
        // 设置死信交换机名称
        paramMap.put("x-dead-letter-exchange", DEAD_QUEUE_DEAD_EXCHANGE_NAME);
        // 设置死信交换机的 routing key
        paramMap.put("x-dead-letter-routing-key", DEAD_QUEUE_DEAD_ROUTING_KEY);
        // 设置过期时间，单位：ms
        // 当消息过期后普通队列会将消息转发到死信队列
        // 一般情况下，消息过期时间会由生产者来设置，这样每条消息可以设置不同的过期时间。
        // paramMap.put("x-message-ttl", 10000);

        // 设置队列最大长度，一旦消息数量超出最大长度，超出的部门会成为死信消息
        // paramMap.put("x-max-length", DEAD_QUEUE_NORMAL_QUEUE_MAX_LENGTH );
        channel.queueDeclare(DEAD_QUEUE_NORMAL_QUEUE_NAME, false, false, false,
                paramMap);
        // 声明死信队列
        channel.queueDeclare(DEAD_QUEUE_DEAD_QUEUE_NAME, false, false, false, null);

        // 将普通交换机与普通队列通过 routing key 绑定
        channel.queueBind(DEAD_QUEUE_NORMAL_QUEUE_NAME, DEAD_QUEUE_NROMAL_EXCHANGE_NAME,
                DEAD_QUEUE_NORMAL_ROUTING_KEY);
        // 将死信交换机与死信队列通过 routing key 绑定
        channel.queueBind(DEAD_QUEUE_DEAD_QUEUE_NAME, DEAD_QUEUE_DEAD_EXCHANGE_NAME,
                DEAD_QUEUE_DEAD_ROUTING_KEY);

        System.out.println("消费者：" + className + " 等待接收消息...");

        // 接收消息并消费
        final String rejectMsg = "info5";
        channel.basicConsume(
                DEAD_QUEUE_NORMAL_QUEUE_NAME,
                // 关闭自动应答，如果自动应答则不存在拒绝消费的情况
                false,
                (consumerTag, msg) -> {
                    final String msgBody = new String(msg.getBody(), UTF_8);
                    // 拒绝接收 info5 消息
                    final long deliveryTag = msg.getEnvelope().getDeliveryTag();
                    if (rejectMsg.equals(msgBody)) {
                        System.out.println("消费者：" + className + " 拒绝接收消息：" + msgBody);
                        // 第二个参数表示是否需要重新放回队列，false 表示否，
                        // 这就表示消费者拒绝消费该消息，消息将变为死信消息，随后队列会将消息转发到死信队列中
                        channel.basicReject(deliveryTag, false);
                    } else {
                        System.out.println("消费者：" + className + "接收到消息：" + msgBody);
                        // 手动应答
                        channel.basicAck(deliveryTag, false);
                    }
                },
                consumerTag -> {
                }
        );
    }
}
