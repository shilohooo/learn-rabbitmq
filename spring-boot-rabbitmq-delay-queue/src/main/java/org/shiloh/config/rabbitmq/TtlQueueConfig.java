package org.shiloh.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMq - 延迟队列配置
 *
 * @author shiloh
 * @date 2022/5/12 23:03
 */
@Configuration
public class TtlQueueConfig {
    /**
     * 普通交换机名称
     */
    private static final String NORMAL_EXCHANGE_NAME = "delay-normal-exchange";

    /**
     * 普通交换机 routing key 01
     */
    private static final String NORMAL_ROUTING_KEY_01 = "delay-normal-rk-01";

    /**
     * 普通交换机 routing key 01
     */
    private static final String NORMAL_ROUTING_KEY_02 = "delay-normal-rk-02";

    /**
     * 死信交换机名称
     */
    private static final String DEAD_LETTER_EXCHANGE_NAME = "delay-dead-letter-exchange";

    /**
     * 死信交换机 routing key
     */
    private static final String DEAD_LETTER_ROUTING_KEY = "delay-dead-letter-rk";

    /**
     * 普通队列1名称
     */
    private static final String NORMAL_QUEUE_NAME_01 = "delay-normal-queue-01";

    /**
     * 普通队列2名称
     */
    private static final String NORMAL_QUEUE_NAME_02 = "delay-normal-queue-02";

    /**
     * 死信队列名称
     */
    private static final String DEAD_LETTER_QUEUE_NAME = "delay-dead-letter-queue";

    /**
     * 延迟队列 - 普通交换机配置
     *
     * @author shiloh
     * @date 2022/5/12 23:09
     */
    @Bean("normalDirectExchange")
    public DirectExchange normalDirectExchange() {
        return new DirectExchange(NORMAL_EXCHANGE_NAME);
    }

    /**
     * 延迟队列 - 死信交换机配置
     *
     * @author shiloh
     * @date 2022/5/12 23:11
     */
    @Bean("deadLetterDirectExchange")
    public DirectExchange deadLetterDirectExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE_NAME);
    }

    /**
     * 延迟队列 - 普通队列1配置
     * 该队列需要配置 TTL，即消息有效时间
     * 这里设置消息有效时间为：10 秒
     *
     * @author shiloh
     * @date 2022/5/12 23:12
     */
    @Bean("normalQueue01")
    public Queue normalQueue01() {
        final Map<String, Object> paramMap = new HashMap<>(3);
        // 设置要将死信消息转发到哪个死信交换机中
        paramMap.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE_NAME);
        // 设置与死信交换机绑定时使用的 routing key
        paramMap.put("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY);
        // 设置消息有效时间，单位：ms
        paramMap.put("x-message-ttl", 10000);
        return QueueBuilder.durable(NORMAL_QUEUE_NAME_01)
                .withArguments(paramMap)
                .build();
    }

    /**
     * 延迟队列 - 普通队列2配置
     * 该队列需要配置 TTL，即消息有效时间
     * 这里设置消息有效时间为：40 秒
     *
     * @author shiloh
     * @date 2022/5/12 23:12
     */
    @Bean("normalQueue02")
    public Queue normalQueue02() {
        final Map<String, Object> paramMap = new HashMap<>(3);
        // 设置要将死信消息转发到哪个死信交换机中
        paramMap.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE_NAME);
        // 设置与死信交换机绑定时使用的 routing key
        paramMap.put("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY);
        // 设置消息有效时间，单位：ms
        paramMap.put("x-message-ttl", 40000);
        return QueueBuilder.durable(NORMAL_QUEUE_NAME_02)
                .withArguments(paramMap)
                .build();
    }

    /**
     * 延迟队列 - 死信队列配置
     *
     * @author shiloh
     * @date 2022/5/12 23:18
     */
    @Bean("deadLetterQueue")
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE_NAME)
                .build();
    }

    /**
     * 延迟队列 - 配置普通队列1 与 普通交换机的绑定关系
     *
     * @author shiloh
     * @date 2022/5/12 23:22
     */
    @Bean
    public Binding normalQueue01ExchangeBind(@Qualifier("normalQueue01") Queue queue,
                                             @Qualifier("normalDirectExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue)
                .to(directExchange)
                .with(NORMAL_ROUTING_KEY_01);
    }

    /**
     * 延迟队列 - 配置普通队列2 与 普通交换机的绑定关系
     *
     * @author shiloh
     * @date 2022/5/12 23:22
     */
    @Bean
    public Binding normalQueue02ExchangeBind(@Qualifier("normalQueue02") Queue queue,
                                             @Qualifier("normalDirectExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue)
                .to(directExchange)
                .with(NORMAL_ROUTING_KEY_02);
    }

    /**
     * 延迟队列 - 配置死信队列 与 死信交换机的绑定关系
     *
     * @author shiloh
     * @date 2022/5/12 23:22
     */
    @Bean
    public Binding deadLetterQueue02ExchangeBind(@Qualifier("deadLetterQueue") Queue queue,
                                             @Qualifier("deadLetterDirectExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue)
                .to(directExchange)
                .with(DEAD_LETTER_ROUTING_KEY);
    }
}
