package org.shiloh.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.shiloh.config.AdvancePublishConfirmConfig.CONFIRM_EXCHANGE_NAME;
import static org.shiloh.config.AdvancePublishConfirmConfig.CONFIRM_ROUTING_KEY;

/**
 * 生产者接口
 *
 * @author shiloh
 * @date 2022/5/23 21:38
 */
@RestController
@RequestMapping("/advance-publish-confirm")
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@Slf4j
public class ConfirmProducerController {
    private final RabbitTemplate rabbitTemplate;

    /**
     * 测试发布确认高级
     *
     * @author shiloh
     * @date 2022/5/23 21:41
     */
    @GetMapping("/test")
    public void testPublishConfirm(String msg) {
        // 发送消息
        this.rabbitTemplate.convertAndSend(
                CONFIRM_EXCHANGE_NAME,
                CONFIRM_ROUTING_KEY,
                msg + CONFIRM_ROUTING_KEY,
                new CorrelationData("1")
        );
        log.info("发送消息内容：'{}' 到 使用了 routing key：{} 的队列中。", msg + CONFIRM_ROUTING_KEY, CONFIRM_ROUTING_KEY);

        this.rabbitTemplate.convertAndSend(
                CONFIRM_EXCHANGE_NAME,
                CONFIRM_ROUTING_KEY + "233",
                msg + CONFIRM_ROUTING_KEY + "233",
                new CorrelationData("2")
        );
        log.info("发送消息内容：'{}' 到使用了 routing key：{} 的队列中。", msg + CONFIRM_ROUTING_KEY + "233",
                CONFIRM_ROUTING_KEY + "233");
    }
}
