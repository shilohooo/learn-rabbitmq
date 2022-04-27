package org.shiloh.rabbitmq.publishconfirm.single;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import org.shiloh.rabbitmq.util.RabbitMqUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 发布确认模式测试
 * <p>
 * 发布确认有以下三种模式：
 * <ol>
 *     <li>单个发布确认，发送 1000 条消息到云服务器上的 MQ 耗时大概 12000 秒</li>
 *     <li>批量发布确认，发送 1000 条消息到云服务器上的 MQ 耗时大概 233 毫秒</li>
 *     <li>异步批量发布确认，发布 1000 条消息到云服务器上的 MQ 大概耗时 32 毫秒</li>
 * </ol>
 * <p>
 * 通过记录以上三种发布确认模式所消耗的时间，来比较哪一种模式较好。
 *
 * @author shiloh
 * @date 2022/4/26 22:12
 */
public class PublishConfirmTests {
    /**
     * 消息批量发送数量
     */
    private static final int MSG_COUNT = 1000;

    public static void main(String[] args) {
        // 1.单个发布确认
        // publishMsgThenSingleConfirm();
        // 2.批量发布确认
        // publishMsgThenBatchConfirm();
        // 3.异步批量发布确认
        publishMsgThenAsyncBatchConfirm();
    }

    /**
     * 单个发布确认
     *
     * @author shiloh
     * @date 2022/4/26 22:16
     */
    public static void publishMsgThenSingleConfirm() {
        // 获取 channel
        try {
            final Channel channel = RabbitMqUtils.getChannel();
            // 创建随机队列
            final String queueName = UUID.randomUUID().toString();
            channel.queueDeclare(queueName, false, false, false, null);
            // 开启发布确认功能
            channel.confirmSelect();
            // 记录开始时间
            final long startTime = System.currentTimeMillis();
            for (int i = 0; i < MSG_COUNT; i++) {
                final String msg = String.valueOf(i);
                // 发送消息到 MQ 队列中
                channel.basicPublish("", queueName, null, msg.getBytes(UTF_8));
                // 发送完成后进行单个发布确认，等候 MQ 确认
                try {
                    final boolean isConfirmed = channel.waitForConfirms();
                    if (isConfirmed) {
                        System.out.println("消息：" + msg + " 发送成功");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // 记录结束时间
            final long endTime = System.currentTimeMillis();
            final long duration = (endTime - startTime);
            System.out.println("发布：" + MSG_COUNT + " 条单独确认消息共耗时" + duration + "ms");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量发布确认
     * <p>
     * 该模式有一个缺点：当消息未确认成功时，无法知道是哪个消息未确认。
     *
     * @author shiloh
     * @date 2022/4/26 22:29
     */
    public static void publishMsgThenBatchConfirm() {
        // 获取 channel
        try {
            final Channel channel = RabbitMqUtils.getChannel();
            // 创建随机队列
            final String queueName = UUID.randomUUID().toString();
            channel.queueDeclare(queueName, false, false, false, null);
            // 开启发布确认功能
            channel.confirmSelect();
            // 记录开始时间
            final long startTime = System.currentTimeMillis();

            // 批量发布消息且使用批量发布确认
            // 每发送 100 条就确认一次
            // 记录未确认的个数
            final int batchConfirmSize = 100;
            for (int i = 0; i < MSG_COUNT; i++) {
                final String msg = String.valueOf(i);
                channel.basicPublish("", queueName, null, msg.getBytes(UTF_8));
                // 当发送了一百条数据后就确认一次消息是否发送成功
                if (((i + 1) % 100) == 0) {
                    try {
                        final boolean isConfirmed = channel.waitForConfirms();
                        if (isConfirmed) {
                            System.out.println("批量发送：" + batchConfirmSize + " 条消息确认成功");
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            // 记录结束时间
            final long endTime = System.currentTimeMillis();
            final long duration = (endTime - startTime);
            System.out.println("批量发布：" + MSG_COUNT + " 条确认消息共耗时" + duration + "ms");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 异步批量发布确认
     *
     * @author shiloh
     * @date 2022/4/26 22:41
     */
    public static void publishMsgThenAsyncBatchConfirm() {
        // 获取 channel
        try {
            final Channel channel = RabbitMqUtils.getChannel();
            // 创建随机队列
            final String queueName = UUID.randomUUID().toString();
            channel.queueDeclare(queueName, false, false, false, null);
            // 开启发布确认功能
            channel.confirmSelect();
            // 准备线程安全、有序的哈希表，适用于高并发场景
            // 该容器具有如下功能：
            // 1.可以为消息标记（序号）和消息建立映射关系
            // 2.可以通过消息标记（序号）删除消息
            // 3.支持高并发（重点功能）
            final ConcurrentSkipListMap<Long, String> outsideConfirmMap = new ConcurrentSkipListMap<>();

            // 准备消息发送监听器，监听哪些消息发送成功了，哪些消息发送失败了
            // 消息确认成功回调处理
            final ConfirmCallback ackCallback = (deliverTag, multiple) -> {
                // 从消息队列中移除已发布确认成功的消息，剩下的就是未确认发布的消息
                System.out.println("是否批量发布确认 = " + multiple);
                if (multiple) {
                    // 批量清理
                    final ConcurrentNavigableMap<Long, String> confirmedMsgMap = outsideConfirmMap.headMap(deliverTag);
                    System.out.println("批量发布确认消息个数 = " + confirmedMsgMap.size());
                    confirmedMsgMap.clear();
                } else {
                    // 单个确认清理
                    outsideConfirmMap.remove(deliverTag);
                }
                System.out.println("确认发布成功的消息标记 = " + deliverTag);
            };
            // 消息确认失败回调处理
            // 第一个参数表示：消息在队列中的标记
            // 第二个参数表示：是否为批量确认
            final ConfirmCallback nackCallback = (deliverTag, multiple) -> {
                // 打印未确认的消息都有哪些，如果有的话
                // 取出未发布确认的消息
                final String notConfirmMsg = outsideConfirmMap.get(deliverTag);
                System.out.println("未发布确认的消息标记 = " + deliverTag);
                System.out.println("未发布确认的消息 = " + notConfirmMsg);
            };

            // 第一个参数表示：消息确认成功的监听器
            // 第二个参数表示：消息确认失败的监听器
            channel.addConfirmListener(ackCallback, nackCallback);

            // 记录开始时间
            final long startTime = System.currentTimeMillis();

            // 批量发布消息，然后使用异步批量发布确认
            for (int i = 0; i < MSG_COUNT; i++) {
                final String msg = String.valueOf(i);
                channel.basicPublish("", queueName, null, msg.getBytes(UTF_8));
                // 由于使用的是异步批量发布确认模式，发布消息和批量确认使用的是不同的线程
                // 这里使用并发队列记录所有要发送的消息，也就是消息的总和
                final long msgTag = channel.getNextPublishSeqNo();
                outsideConfirmMap.put(msgTag, msg);
            }

            // 记录结束时间
            final long endTime = System.currentTimeMillis();
            final long duration = (endTime - startTime);
            System.out.println("异步批量发布：" + MSG_COUNT + " 条确认消息共耗时" + duration + "ms");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
