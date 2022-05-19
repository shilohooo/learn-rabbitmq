package org.shiloh.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 延迟队列 - 基于延迟消息交换机插件的配置
 *
 * @author shiloh
 * @date 2022/5/19 21:16
 */
@Configuration
public class DelayedQueueConfig {
    /**
     * 延迟消息交换机名称
     */
    public static final String PLUGIN_DELAYED_EXCHANGE_NAME = "plugin_delayed_exchange";

    /**
     * 延迟交换机类型
     */
    private static final String PLUGIN_DELAYED_EXCHANGE_TYPE = "x-delayed-message";

    /**
     * 队列名称
     */
    public static final String PLUGIN_DELAYED_QUEUE_NAME = "plugin_delayed_queue";

    /**
     * routing key
     */
    public static final String PLUGIN_DELAYED_RK = "plugin_delayed_rk";

    /**
     * 自定义延迟交换机配置
     *
     * @return 自定义延迟交换机 Bean
     * @author shiloh
     * @date 2022/5/19 21:19
     */
    @Bean
    public CustomExchange delayedExchange() {
        final Map<String, Object> paramMap = new HashMap<>(3);
        // 指定延迟类型参数，这里指定的类型是交换机处理消息的机制，即怎么发送消息
        // 而交换机类型则是指定消息什么时候发送
        paramMap.put("x-delayed-type", "direct");
        return new CustomExchange(
                // 交换机名称
                PLUGIN_DELAYED_EXCHANGE_NAME,
                // 交换机类型：插件指定类型：x-delayed-message
                PLUGIN_DELAYED_EXCHANGE_TYPE,
                // 是否需要持久化
                true,
                // 是否自动删除消息
                false,
                // 其它参数
                paramMap
        );
    }

    /**
     * 延迟队列配置
     *
     * @return 延迟队列 Bean
     * @author shiloh
     * @date 2022/5/19 21:27
     */
    @Bean
    public Queue delayedQueue() {
        return QueueBuilder.durable(PLUGIN_DELAYED_QUEUE_NAME)
                .build();
    }

    /**
     * 配置延迟交换机与队列的绑定关系
     *
     * @param delayedQueue    延迟队列 Bean
     * @param delayedExchange 延迟交换机 Bean
     * @return 绑定关系 Bean
     * @author shiloh
     * @date 2022/5/19 21:25
     */
    @Bean
    public Binding delayedQueueExchangeBinding(@Qualifier("delayedQueue") Queue delayedQueue,
                                               @Qualifier("delayedExchange") CustomExchange delayedExchange) {
        return BindingBuilder.bind(delayedQueue)
                .to(delayedExchange)
                .with(PLUGIN_DELAYED_RK)
                .noargs();
    }
}
