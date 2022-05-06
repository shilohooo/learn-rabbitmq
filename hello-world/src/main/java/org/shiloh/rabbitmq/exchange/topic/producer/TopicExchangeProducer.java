package org.shiloh.rabbitmq.exchange.topic.producer;

import com.rabbitmq.client.Channel;
import org.shiloh.rabbitmq.util.RabbitMqUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.shiloh.rabbitmq.constant.RabbitMqConstants.TOPIC_EXCHANGE_NAME;

/**
 * 主题交换机案例 - 生产者
 *
 * @author shiloh
 * @date 2022/5/6 21:37
 */
public class TopicExchangeProducer {
    /**
     * 消息Map
     */
    private static final Map<String, String> MSG_MAP = new HashMap<>(3);

    static {
        // 数据模拟
        MSG_MAP.put("quick.orange.rabbit", "这条消息将会被队列：topic_queue_01、topic_queue_02接收到");
        MSG_MAP.put("lazy.orange.elephant", "这条消息将会被队列：topic_queue_01、topic_queue_02接收到");
        MSG_MAP.put("quick.orange.fox", "这条消息将会被队列：topic_queue_01接收到");
        MSG_MAP.put("lazy.brown.fox", "这条消息将会被队列：topic_queue_02接收到");
        MSG_MAP.put("lazy.pink.rabbit", "这条消息将会被队列：topic_queue_02接收到，但只会被消费一次");
        MSG_MAP.put("quick.brown.fox", "这条消息没有匹配到任何主题，不会被队列接收，将会被丢弃");
        MSG_MAP.put("quick.orange.male.rabbit", "这条消息没有匹配到任何主题，不会被队列接收，将会被丢弃");
        MSG_MAP.put("lazy.orange.male.rabbit", "这条消息将会被队列：topic_queue_02接收到");
    }

    public static void main(String[] args) {
        try {
            final Channel channel = RabbitMqUtils.getChannel();
            // 发送消息到不同的主题中
            // 目前有2个队列：topic_queue_01、topic_queue_02
            // topic_queue_01绑定的主题为：*.orange.*
            // topic_queue_02绑定的主题为：*.*.rabbit、lazy.#

            // 使用Map数据格式，将主题作为 key，消息作为 value，进行遍历发送
            final String className = TopicExchangeProducer.class.getSimpleName();
            MSG_MAP.forEach((key, val) -> {
                try {
                    channel.basicPublish(TOPIC_EXCHANGE_NAME, key, null, val.getBytes(UTF_8));
                    System.out.println(className + " 生产者发送消息：" + val + " 成功");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
