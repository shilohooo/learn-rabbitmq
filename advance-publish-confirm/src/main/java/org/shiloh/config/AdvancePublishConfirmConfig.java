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
     * 备份交换机名称
     * 备份交换机：交换机的备胎，当生产者的消息无法发送到交换机中，可以将消息转发到备份交换机中，
     * 由备份交换机处理，备份交换机绑定的备份队列、报警队列可以备份发送失败的消息，以及拥有报警功能。
     */
    public static final String BACKUP_EXCHANGE_NAME = "backup.exchange";

    /**
     * 备份队列名称
     */
    public static final String BACKUP_QUEUE_NAME = "backup.queue";

    /**
     * 报警队列名称
     */
    public static final String WARNING_QUEUE_NAME = "warning.queue";

    /**
     * 交换机配置
     *
     * @author shiloh
     * @date 2022/5/23 21:36
     */
    @Bean
    public DirectExchange confirmExchange() {
        return ExchangeBuilder.directExchange(CONFIRM_EXCHANGE_NAME)
                .durable(true)
                // 绑定备份交换机
                // 当生产者无法将将消息发布到该交换机时，消息将被转发到备份交换机中进行处理
                // 当回退机制与备份交换机一起使用时，备份交换机的优先级比回退机制要高。
                .withArgument("alternate-exchange", BACKUP_EXCHANGE_NAME)
                .build();
    }

    /**
     * 队列配置
     *
     * @author shiloh
     * @date 2022/5/23 21:36
     */
    @Bean
    public Queue confirmQueue() {
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME)
                // 设置队列的优先级，0 - 255，数值越大优先级越高
                // .withArgument("x-max-priority", 10)
                .build();
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

    /**
     * 配置备份交换机
     *
     * @return 交换机 Bean
     * @author shiloh
     * @date 2022/5/29 15:48
     */
    @Bean
    public FanoutExchange backupExchange() {
        return new FanoutExchange(BACKUP_EXCHANGE_NAME);
    }

    /**
     * 配置备份队列
     *
     * @return 备份队列 Bean
     * @author shiloh
     * @date 2022/5/29 15:50
     */
    @Bean
    public Queue backupQueue() {
        return QueueBuilder.durable(BACKUP_QUEUE_NAME)
                .build();
    }

    /**
     * 配置报警队列
     *
     * @return 报警队列 Bean
     * @author shiloh
     * @date 2022/5/29 15:51
     */
    @Bean
    public Queue warningQueue() {
        return QueueBuilder.durable(WARNING_QUEUE_NAME)
                .build();
    }

    /**
     * 配置备份交换机与备份队列的绑定关系
     *
     * @param backupExchange 备份交换机
     * @param backupQueue    备份队列
     * @return 绑定关系 Bean
     * @author shiloh
     * @date 2022/5/29 15:54
     */
    @Bean
    public Binding backupQueueExchangeBind(@Qualifier("backupExchange") FanoutExchange backupExchange,
                                           @Qualifier("backupQueue") Queue backupQueue) {
        return BindingBuilder.bind(backupQueue)
                .to(backupExchange);
    }

    /**
     * 配置备份交换机与报警队列的绑定关系
     *
     * @param backupExchange 备份交换机
     * @param warningQueue   报警队列
     * @return 绑定关系 Bean
     * @author shiloh
     * @date 2022/5/29 15:54
     */
    @Bean
    public Binding warningQueueExchangeBind(@Qualifier("backupExchange") FanoutExchange backupExchange,
                                            @Qualifier("warningQueue") Queue warningQueue) {
        return BindingBuilder.bind(warningQueue)
                .to(backupExchange);
    }
}
