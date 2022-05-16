package org.shiloh.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

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
     * 发送指定 TTL 的延迟消息
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
        this.rabbitTemplate.convertAndSend("delay-normal-exchange", "non-delay-normal-rk", msg,
                message -> {
                    // 设置消息存活时间
                    message.getMessageProperties()
                            .setExpiration(ttlTime);
                    return message;
                });
    }
}
