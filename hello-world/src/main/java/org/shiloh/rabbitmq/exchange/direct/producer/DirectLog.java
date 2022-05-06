package org.shiloh.rabbitmq.exchange.direct.producer;

import com.rabbitmq.client.Channel;
import org.shiloh.rabbitmq.util.RabbitMqUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import static com.rabbitmq.client.BuiltinExchangeType.DIRECT;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.shiloh.rabbitmq.constant.RabbitMqConstants.DIRECT_EXCHANGE_NAME;

/**
 * 直接交换机案例 - 日志消息生产者，负责发布日志消息到交换机中
 *
 * @author shiloh
 * @date 2022/4/27 22:14
 */
public class DirectLog {
    public static void main(String[] args) {
        try {
            // get channel
            final Channel channel = RabbitMqUtils.getChannel();
            // 创建交换机并指定名称和类型
            channel.exchangeDeclare(DIRECT_EXCHANGE_NAME, DIRECT);
            // 从控制台读取输入以获取消息数据，并发布到交换机中
            final Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                final String msg = scanner.next();
                // 根据不同的 routing key 将消息发送到不同的队列中
                // 目前有的 routing key 有：info、warning、error
                channel.basicPublish(DIRECT_EXCHANGE_NAME, "error", null, msg.getBytes(UTF_8));
                System.out.println("已将消息：" + msg + " 成功发布到交换机：" + DIRECT_EXCHANGE_NAME);
            }
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
