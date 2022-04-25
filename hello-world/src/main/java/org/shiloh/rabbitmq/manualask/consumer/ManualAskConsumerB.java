package org.shiloh.rabbitmq.manualask.consumer;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.shiloh.rabbitmq.util.RabbitMqUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.shiloh.rabbitmq.constant.RabbitMqConstants.MANUAL_ASK_QUEUE;

/**
 * 手动应答的消费者，若消费失败，则在应答时消息不能丢失，需要放回队列中重新消费。
 *
 * @author shiloh
 * @date 2022/4/21 21:23
 */
public class ManualAskConsumerB {
    public static void main(String[] args) {
        final String className = ManualAskConsumerB.class.getSimpleName();
        // 获取 channel
        try {
            final Channel channel = RabbitMqUtils.getChannel();
            System.out.println("等待接收消息处理时间较长的消费者：" + className);
            // 采用手动应答的方式处理消息
            // 声明消息接收成功后的回调处理
            final DeliverCallback deliverCallback = (consumerTag, deliver) -> {
                // 接收前睡眠 30 秒种，模拟处理超时的情况
                try {
                    TimeUnit.SECONDS.sleep(30L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                final String msg = new String(deliver.getBody(), UTF_8);
                System.out.println("消费者：" + className + " 接收到消息：" + msg);
                // 手动应答，通知 MQ 消费已被成功消费，可以从队列中删掉了
                // 参数说明：
                // 第一个参数 deliveryTag ：表示消息在队列中的 tag 标记值
                // 第二个消息 multiple ：表示是否批量应答，这里传 false，处理一个消息就应答一次，不影响其他消息。
                channel.basicAck(deliver.getEnvelope().getDeliveryTag(), false);
            };
            // 定义取消消费或消费被中断后的回调处理
            final CancelCallback cancelCallback = consumerTag -> System.out.println(
                    consumerTag + "消费者：" + className + " 取消了消费或消费过程被中断了:(");
            // 设置不公平分发模式
            // 默认为0，即采用轮询分发模式
            // 当 prefetchCount 大于 1 时，表示采用预取值模式，即代表该消费者会收到 prefetchCount 条消息，不论处理速度如何。
            // 这里设置预取值为 5，表示该消费者会接收到 5 条消息，也可能由于消息处理速度的原因，接收到的消息数量与预取值不一致。
            // 但该消费者未接收的消息个数不会超过 5 个
            final int prefetchCount = 5;
            channel.basicQos(prefetchCount);
            channel.basicConsume(MANUAL_ASK_QUEUE, false, deliverCallback, cancelCallback);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
