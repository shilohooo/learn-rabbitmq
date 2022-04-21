package org.shiloh.rabbitmq.worker;

import com.rabbitmq.client.Channel;
import org.shiloh.rabbitmq.util.RabbitMqUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.shiloh.rabbitmq.constant.RabbitMqConstants.QUEUE_NAME;

/**
 * 工作线程A，相当于一个消费者
 *
 * @author shiloh
 * @date 2022/4/21 20:42
 */
public class WorkerA {
    public static void main(String[] args) {
        // 获取 channel
        try {
            final Channel channel = RabbitMqUtils.getChannel();
            System.out.println("Consumer2等待接收消息中...");
            // 接收消息
            final String className = WorkerA.class.getSimpleName();
            channel.basicConsume(
                    QUEUE_NAME,
                    true,
                    // 消息接收成功触发的回调处理
                    (consumerTag, msg) -> System.out.println(
                            className + " 接收到的消息为：" + new String(msg.getBody(), UTF_8)
                    ),
                    // 消息接收被取消或中断时触发的回调
                    consumerTag -> System.out.println(consumerTag + className + "取消接收消息或接收过程意外中断:(")
            );

        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
