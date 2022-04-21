package org.shiloh.rabbitmq.util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.shiloh.rabbitmq.constant.RabbitMqConstants.Server.*;

/**
 * Rabbit MQ 工具
 *
 * @author shiloh
 * @date 2022/4/21 20:38
 */
public final class RabbitMqUtils {
    private RabbitMqUtils() {

    }

    /**
     * 获取 Channel {@link Channel}
     *
     * @author shiloh
     * @date 2022/4/21 20:39
     */
    public static Channel getChannel() throws IOException, TimeoutException {
        // 1.创建连接工厂
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        // 设置 MQ 服务器 IP 和端口，用于后续连接 MQ 队列
        connectionFactory.setHost(IP);
        connectionFactory.setPort(PORT);
        // 设置用于登录认证的用户名和密码
        connectionFactory.setUsername(USERNAME);
        connectionFactory.setPassword(PASSWORD);
        // 创建连接
        final Connection connection = connectionFactory.newConnection();
        // 创建 channel 并返回
        return connection.createChannel();
    }
}
