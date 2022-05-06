package org.shiloh.rabbitmq.exchange.fanout.consumer;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.shiloh.rabbitmq.util.RabbitMqUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.rabbitmq.client.BuiltinExchangeType.FANOUT;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.shiloh.rabbitmq.constant.RabbitMqConstants.LOG_MSG_FANOUT_EXCHANGE_NAME;

/**
 * 交换机测试 - 日志消费者01
 *
 * @author shiloh
 * @date 2022/4/27 21:54
 */
public class ReceiveLog1 {

    public static void main(String[] args) {
        try {
            // 获取 channel
            final Channel channel = RabbitMqUtils.getChannel();
            // 声明一个交换机，指定交换机的名称，以及指定交换机的类型为发布/订阅（fanout）类型。
            channel.exchangeDeclare(LOG_MSG_FANOUT_EXCHANGE_NAME, FANOUT.getType());
            // 创建一个临时队列，该队列的名称是随机的
            // 当消费者断开与队列的连接后，该队列会被自动删除
            final String queue = channel.queueDeclare()
                    .getQueue();
            // 绑定交换机与队列的关系
            channel.queueBind(queue, LOG_MSG_FANOUT_EXCHANGE_NAME, "");
            System.out.println("日志消费者01等待接收消息，把接收到的消息打印到控制台~");

            // 接收消息成功回调处理
            final DeliverCallback deliverCallback = (consumerTag, message) ->
                    System.out.println("日志消费者01接收到消息：" + new String(message.getBody(), UTF_8));
            // 消息接收失败或接收过程被中断的回调处理
            final CancelCallback cancelCallback = consumerTag ->
                    System.out.println("日志消费者01接收消息失败:(");
            channel.basicConsume(queue, true, deliverCallback, cancelCallback);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
