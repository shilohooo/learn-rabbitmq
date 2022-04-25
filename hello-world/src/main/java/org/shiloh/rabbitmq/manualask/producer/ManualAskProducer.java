package org.shiloh.rabbitmq.manualask.producer;

import com.rabbitmq.client.Channel;
import org.shiloh.rabbitmq.manualask.consumer.ManualAskConsumerA;
import org.shiloh.rabbitmq.manualask.consumer.ManualAskConsumerB;
import org.shiloh.rabbitmq.util.RabbitMqUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import static com.rabbitmq.client.MessageProperties.PERSISTENT_TEXT_PLAIN;
import static java.nio.charset.StandardCharsets.UTF_8;
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
            // 开启发布确认功能，该功能的作用是：
            // 当生产者将消息发送到 MQ 后，MQ可以告诉生产者消息已经成功发布了。
            channel.confirmSelect();
            // 创建队列
            // 指定此队列需要开启持久化功能
            final boolean durable = true;
            channel.queueDeclare(MANUAL_ASK_QUEUE, durable, false, false, null);
            // 获取控制台输入，将输入结果作为消息发送到 MQ
            final Scanner scanner = new Scanner(System.in);
            final String className = ManualAskProducer.class.getSimpleName();
            while (scanner.hasNext()) {
                final String msg = scanner.next();
                // 发送消息到 MQ 队列（依然是使用默认的交换机）
                // 让消息在队列中实现持久化需要传递特定属性
                // MessageProperties.PERSISTENT_TEXT_PLAIN ：表示需要持久化消息到磁盘中
                // 如果没有指定该属性，消息默认只保留在内存中
                // 当队列以及消息都开启持久化功能后，便能保证在简单的情况下不会丢失消息了
                channel.basicPublish("", MANUAL_ASK_QUEUE, PERSISTENT_TEXT_PLAIN, msg.getBytes(UTF_8));
                System.out.println("生产者：" + className + " 发送消息：" + msg);
            }
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}

