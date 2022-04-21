package org.shiloh.rabbitmq.producer;

import com.rabbitmq.client.Channel;
import org.shiloh.rabbitmq.util.RabbitMqUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import static org.shiloh.rabbitmq.constant.RabbitMqConstants.QUEUE_NAME;

/**
 * 可以发送大量消息的生产者
 *
 * @author shiloh
 * @date 2022/4/21 20:57
 */
public class ProducerTask {
    public static void main(String[] args) throws IOException, TimeoutException {
        // 发送大量消息到 MQ 队列中供多个消费者消费
        final Channel channel = RabbitMqUtils.getChannel();
        // 定义队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // 从控制台输入消息然后发送到 MQ 队列中
        final Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            final String msg = scanner.next();
            // 使用默认的交换机发送消息
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes(StandardCharsets.UTF_8));
            System.out.println("消息内容：" + msg + " 已发送到 MQ 队列中。");
        }
    }
}
