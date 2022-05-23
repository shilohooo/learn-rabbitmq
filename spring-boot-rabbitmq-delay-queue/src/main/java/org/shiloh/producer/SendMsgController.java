package org.shiloh.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static org.shiloh.config.DelayedQueueConfig.PLUGIN_DELAYED_EXCHANGE_NAME;
import static org.shiloh.config.DelayedQueueConfig.PLUGIN_DELAYED_RK;

/**
 * 生产者 - 延迟消息发送接口
 *
 * @author shiloh
 * @date 2022/5/16 21:55
 */
@RestController
@RequestMapping("/send-msg")
@Slf4j
@RequiredArgsConstructor
public class SendMsgController {
    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送延迟消息
     *
     * @param msg 消息数据
     * @author shiloh
     * @date 2022/5/16 21:56
     */
    @GetMapping("/ttl")
    public void sendDelayMsg(String msg) {
        log.info("当前时间：{}，发送一条信息给两个 TTL 队列，消息内容：{}", new Date(), msg);
        // 发送消息
        this.rabbitTemplate.convertAndSend("delay-normal-exchange", "delay-normal-rk-01",
                "消息来自 TTL 为 10 秒的延迟队列：" + msg);
        this.rabbitTemplate.convertAndSend("delay-normal-exchange", "delay-normal-rk-02",
                "消息来自 TTL 为 40 秒的延迟队列：" + msg);
    }

    /**
     * 发送指定 TTL 的延迟消息，使用死信队列作为延迟队列的缺陷：
     * 若队列中存在多个设置了 TTL 的队列，而第一个入队的消息 TTL 较长，且随后入队的消息 TTL 较短，
     * 此时 RabbitMq 只会检查队列中第一个消息是否过期，如果过期则将其转发到死信队列，但由于第一个消息延时很长，
     * 而其余消息延迟较短，其余消息并不会有限得到执行。
     * <p>
     * 这是因为队列是先进先出的数据结构，当第一条消息延迟过长时，剩余的消息便被阻塞在队列中了，
     * 需要等到第一个消息过期被转发到死信队列后，其余消息才有机会执行。
     *
     * @param msg     消息内容
     * @param ttlTime 消息存活时间，单位：毫秒
     * @author shiloh
     * @date 2022/5/16 22:36
     */
    @GetMapping("/send-with-specific-ttl")
    public void sendDelayMsgWithSpecificTtl(String msg, String ttlTime) {
        log.info("当前时间：{}，发送一条 TTL 为：{} 毫秒的信息给队列：non-delay-queue，消息内容：{}", new Date(), ttlTime,
                msg);
        this.rabbitTemplate.convertAndSend(
                "delay-normal-exchange",
                "non-delay-normal-rk",
                msg,
                message -> {
                    // 设置消息存活时间
                    message.getMessageProperties()
                            .setExpiration(ttlTime);
                    return message;
                }
        );
    }

    /**
     * 延迟队列 - 基于延迟交换机插件发送延迟消息
     *
     * @param msg     消息内容
     * @param ttlTime 延迟时间，单位：毫秒
     * @author shiloh
     * @date 2022/5/19 21:36
     */
    @GetMapping("/delayed-msg-with-plugin")
    public void sendDelayedMsgWithPlugin(String msg, Integer ttlTime) {
        log.info("当前时间：{}，发送一条 TTL 为：{} 毫秒的消息给延迟队列：delayed_queue，消息内容：{}", new Date(), ttlTime, msg);
        this.rabbitTemplate.convertAndSend(
                PLUGIN_DELAYED_EXCHANGE_NAME,
                PLUGIN_DELAYED_RK,
                msg,
                message -> {
                    // 发送消息时，设置延迟时间，单位：毫秒
                    message.getMessageProperties().setDelay(ttlTime);
                    return message;
                }
        );
    }
}
