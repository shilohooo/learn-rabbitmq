package org.shiloh.rabbitmq.producer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.shiloh.rabbitmq.util.RabbitMqUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.shiloh.rabbitmq.constant.RabbitMqConstants.QUEUE_NAME;

/**
 * 生产者，用于将消息发送到 MQ 中
 *
 * @author shiloh
 * @date 2022/4/20 21:26
 */
public class Producer {
    public static void main(String[] args) {
        // 发生消息到MQ指定队列中
        try {
            // 获取 channel
            final Channel channel = RabbitMqUtils.getChannel();
            // 采用默认交换机，创建一个队列
            // 参数说明：
            // 第一个参数 queue ：代表设置队列名称。
            // 第二个参数 durable ：是否需要持久化队列中的消息，消息数据默认存储在内存中，若该参数的值为 true，则会将消息数据存储到磁盘中。
            // 第三个参数 exclusive ：是否进行消息共享，如果为 true，则表示队列中的消息允许多个消费者进行消费。
            // 第四个参数 autoDelete ：该队列在最后一个消费端断开连接后是否自动删除，true = 自动删除。
            // 第五个参数 arguments ：代表其他参数，本例暂时用不上，可以给个 null。

            // 2022-052-29 设置优先级参数
            final Map<String, Object> params = new HashMap<>(1);
            // 优先级的取值范围：0 - 255，值越大优先级越高。
            // 此处设置为10，优先级的范围为：0 - 10
            params.put("x-max-priority", 10);
            channel.queueDeclare(QUEUE_NAME, true, false, false, params);
            for (int i = 1; i <= 10; i++) {
                // 发送消息
                final String msg = "Hello RabbitMQ:)" + i;
                System.out.println("发送消息：" + msg + " 到 MQ 中");
                final byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
                if (i == 5) {
                    // 参数说明：
                    // 第一个参数 exchange ：表示交换机名称，本例中使用的是默认的交换机，传一个空字符串即可。
                    // 第二个参数 routingKey ：在这里代表的是队列名称。
                    // 第三个参数 props ：代表一些属性，本例只是基本的发送消息操作，传 null 即可。
                    // 第四个参数 body ：代表消息体数据，且需要转换为 byte 数据。

                    // 2022-05-29：设置消费消息的优先级
                    final AMQP.BasicProperties basicProperties = new AMQP.BasicProperties()
                            .builder()
                            .priority(i)
                            .build();
                    channel.basicPublish("", QUEUE_NAME, basicProperties, msgBytes);
                } else {
                    channel.basicPublish("", QUEUE_NAME, null, msgBytes);
                }
            }
            System.out.println("消息发送完毕");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
