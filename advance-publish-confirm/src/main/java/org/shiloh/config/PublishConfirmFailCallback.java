package org.shiloh.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 发布确认失败回调配置
 *
 * @author shiloh
 * @date 2022/5/23 21:53
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
public class PublishConfirmFailCallback implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    private final RabbitTemplate rabbitTemplate;

    /**
     * 后置处理：实例化后将该配置类注入到 {@link RabbitTemplate}，
     * <p>
     * 不注入的话就没有可用的回调实现可供调用了~
     *
     * @author shiloh
     * @date 2022/5/23 21:58
     */
    @PostConstruct
    public void init() {
        this.rabbitTemplate.setConfirmCallback(this);
        this.rabbitTemplate.setReturnCallback(this);
    }

    /**
     * 交换机确认回调方法
     *
     * @param correlationData 消息内容对象，保存回调消息的 ID 以及相关信息
     * @param ack             若信息确认成功则为 true，否则为 false
     * @param reason          失败原因
     * @author shiloh
     * @date 2022/5/23 21:53
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String reason) {
        final String id = Objects.nonNull(correlationData) ? correlationData.getId() : "";
        if (ack) {
            log.info("交换机已收到了 ID 为：{} 的消息，发布确认成功", id);
            return;
        }

        log.info("交换机还未收到 ID 为：{} 的消息，发布确认失败，原因为：{}", id, reason);
    }

    /**
     * 消息回退处理，只有当消息在传递过程中无法到达目的时，该方法才会被回调触发
     *
     * @param message    消息对象
     * @param replyCode  回退码
     * @param replyText  回退原因
     * @param exchange   交换机名称
     * @param routingKey routing key
     * @author shiloh
     * @date 2022/5/23 22:19
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        final String msg = new String(message.getBody(), UTF_8);
        log.error("消息内容：'{}' 被交换机：{} 退回，退回原因：{}，routing key：{}", msg, exchange, replyText, replyText);

    }
}
