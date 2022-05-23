package org.shiloh.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 发布确认高级 - 配置类
 *
 * @author shiloh
 * @date 2022/5/23 21:32
 */
@Configuration
public class AdvancePublishConfirmConfig {
    /**
     * 交换机名称
     */
    public static final String CONFIRM_EXCHANGE_NAME = "confirm_exchange";

    /**
     * 队列名称
     */
    public static final String CONFIRM_QUEUE_NAME = "confirm_queue";

    /**
     * routing key
     */
    public static final String CONFIRM_ROUTING_KEY = "confirm_routing_key";

    /**
     * 交换机配置
     *
     * @author shiloh
     * @date 2022/5/23 21:36
     */
    @Bean
    public DirectExchange confirmExchange() {
        return new DirectExchange(CONFIRM_EXCHANGE_NAME);
    }

    /**
     * 队列配置
     *
     * @author shiloh
     * @date 2022/5/23 21:36
     */
    @Bean
    public Queue confirmQueue() {
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }

    /**
     * 队列与交换机绑定配置
     *
     * @author shiloh
     * @date 2022/5/23 21:38
     */
    @Bean
    public Binding confirmQueueExchangeBind(@Qualifier("confirmExchange") DirectExchange directExchange,
                                            @Qualifier("confirmQueue") Queue queue) {
        return BindingBuilder.bind(queue)
                .to(directExchange)
                .with(CONFIRM_ROUTING_KEY);
    }
}
