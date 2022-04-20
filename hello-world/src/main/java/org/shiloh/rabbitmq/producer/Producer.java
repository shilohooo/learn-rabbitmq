package org.shiloh.rabbitmq.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import static org.shiloh.rabbitmq.constant.RabbitMqConstants.QUEUE_NAME;
import static org.shiloh.rabbitmq.constant.RabbitMqConstants.Server.*;

/**
 * 生产者，用于将消息发送到 MQ 中
 *
 * @author shiloh
 * @date 2022/4/20 21:26
 */
public class Producer {
    public static void main(String[] args) {
        // 发生消息到MQ指定队列中
        // 1.创建连接工厂
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        // 设置 MQ 服务器 IP 和端口，用于后续连接 MQ 队列
        connectionFactory.setHost(IP);
        connectionFactory.setPort(PORT);
        // 设置用于登录认证的用户名和密码
        connectionFactory.setUsername(USERNAME);
        connectionFactory.setPassword(PASSWORD);

        // 创建连接
        try (final Connection connection = connectionFactory.newConnection()) {
            // 获取 channel
            final Channel channel = connection.createChannel();
            // 采用默认交换机，创建一个队列
            // 参数说明：
            // 第一个参数 queue ：代表设置队列名称。
            // 第二个参数 durable ：是否需要持久化队列中的消息，消息数据默认存储在内存中，若该参数的值为 true，则会将消息数据存储到磁盘中。
            // 第三个参数 exclusive ：是否进行消息共享，如果为 true，则表示队列中的消息允许多个消费者进行消费。
            // 第四个参数 autoDelete ：该队列在最后一个消费端断开连接后是否自动删除，true = 自动删除。
            // 第五个参数 arguments ：代表其他参数，本例暂时用不上，可以给个 null。
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            // 发送消息
            final String msg = "Hello RabbitMQ:)";
            // 参数说明：
            // 第一个参数 exchange ：表示交换机名称，本例中使用的是默认的交换机，传一个空字符串即可。
            // 第二个参数 routingKey ：在这里代表的是队列名称。
            // 第三个参数 props ：代表一些属性，本例只是基本的发送消息操作，传 null 即可。
            // 第四个参数 body ：代表消息体数据，且需要转换为 byte 数据。
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes(StandardCharsets.UTF_8));
            System.out.println("消息发送完毕");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
