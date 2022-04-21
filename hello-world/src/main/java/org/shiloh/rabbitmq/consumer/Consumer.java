package org.shiloh.rabbitmq.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import static org.shiloh.rabbitmq.constant.RabbitMqConstants.QUEUE_NAME;
import static org.shiloh.rabbitmq.constant.RabbitMqConstants.Server.*;

/**
 * 消费者，用于接收 MQ 中的消息
 *
 * @author shiloh
 * @date 2022/4/20 21:52
 */
public class Consumer {
    public static void main(String[] args) {
        // 创建连接工厂
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        // 设置连接信息
        connectionFactory.setHost(IP);
        connectionFactory.setPort(PORT);
        connectionFactory.setUsername(USERNAME);
        connectionFactory.setPassword(PASSWORD);
        try (final Connection connection = connectionFactory.newConnection()) {
            // 创建 channel
            final Channel channel = connection.createChannel();

            // 定义消费成功后的回调处理
            final DeliverCallback deliverCallback = (consumerTag, msg) -> System.out.println("消费者接收到消息：" + new String(msg.getBody(), StandardCharsets.UTF_8));

            // 定义取消消费或消费被中断后的回调处理，只有当取消消费或消费被中断才会执行
            final CancelCallback cancelCallback = System.out::println;

            // 接收消息
            // 参数说明：
            // 第一个参数 queue ：表示队列名称
            // 第二个参数 autoAsk ：是否会自动答，true = 是
            // 第三个参数 consumerTag ：表示消费者消费成功的回调，可用 lambda 定义。
            // 第四个参数 callback ：表示消费者取消消费或消费被中断的回调，可用 lambda 定义。
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
