package org.shiloh.rabbitmq.constant;

/**
 * RabbitMQ 常量
 *
 * @author shiloh
 * @date 2022/4/20 21:35
 */
public final class RabbitMqConstants {
    private RabbitMqConstants() {

    }

    /**
     * 队列名称
     * <p>
     * 生产者将消息发送到此队列中，消费者将接收该队列中的消息
     */
    public static final String QUEUE_NAME = "Hello";

    /**
     * 手动应答队列
     */
    public static final String MANUAL_ASK_QUEUE = "ManualAskQueue";

    /**
     * 日志消息接收扇出交换机名称
     */
    public static final String LOG_MSG_FANOUT_EXCHANGE_NAME = "logs";

    /**
     * 直接交换机名称，与扇出交换机的区别是：当各队列的 routingKey 不一样时，消息不会被多个队列所消费
     */
    public static final String DIRECT_EXCHANGE_NAME = "direct_logs";

    /**
     * 主题交换机名称
     */
    public static final String TOPIC_EXCHANGE_NAME = "topic_logs";

    /**
     * MQ 服务器相关常量
     *
     * @author shiloh
     * @date 2022/4/20 21:36
     */
    public static class Server {
        /**
         * RabbitMQ 服务器 IP
         */
        public static final String IP = "1.12.253.192";

        /**
         * RabbitMQ 服务器端口
         */
        public static final int PORT = 5672;

        /**
         * 用户名
         */
        public static final String USERNAME = "shiloh";

        /**
         * 密码
         */
        public static final String PASSWORD = "Lixiaolei1998#@";
    }
}
