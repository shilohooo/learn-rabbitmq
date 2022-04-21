package org.shiloh.rabbitmq.manualask.producer;

import com.rabbitmq.client.Channel;
import org.shiloh.rabbitmq.manualask.consumer.ManualAskConsumerA;
import org.shiloh.rabbitmq.manualask.consumer.ManualAskConsumerB;
import org.shiloh.rabbitmq.util.RabbitMqUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import static org.shiloh.rabbitmq.constant.RabbitMqConstants.MANUAL_ASK_QUEUE;

/**
 * 生产者，生产的消息被消费者消费后不自动应答
 * <p>
 * 测试方式：
 * <ol>
 *     <li>
 *        启动生产者 {@link ManualAskProducer}、消费者A {@link ManualAskConsumerA}、消费者B {@link ManualAskConsumerB}
 *     </li>
 *     <li>
 *         发送2个消息到 MQ 中，再发送第二个消息后，关掉消费者B {@link ManualAskConsumerB}
 *     </li>
 *     <li>
 *         观察消费者A {@link ManualAskConsumerA} 是否有接收到第二个消息。
 *     </li>
 * </ol>
 *
 * @author shiloh
 * @date 2022/4/21 21:17
 */
public class ManualAskProducer {
    public static void main(String[] args) {
        // 获取 channel
        try {
            final Channel channel = RabbitMqUtils.getChannel();
            // 创建队列
            channel.queueDeclare(MANUAL_ASK_QUEUE, false, false, false, null);
            // 获取控制台输入，将输入结果作为消息发送到 MQ
            final Scanner scanner = new Scanner(System.in);
            final String className = ManualAskProducer.class.getSimpleName();
            while (scanner.hasNext()) {
                final String msg = scanner.next();
                // 发送消息到 MQ 队列（依然是使用默认的交换机）
                channel.basicPublish("", MANUAL_ASK_QUEUE, null, msg.getBytes(StandardCharsets.UTF_8));
                System.out.println("生产者：" + className + " 发送消息：" + msg);
            }
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}

