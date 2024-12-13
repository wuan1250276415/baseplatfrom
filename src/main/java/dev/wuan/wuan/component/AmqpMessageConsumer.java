package dev.wuan.wuan.component;

import dev.wuan.wuan.dto.amqp.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
/**
 * AMQP消息消费者组件
 * 用于处理来自不同队列的消息
 */
public class AmqpMessageConsumer {

  /**
   * 处理简单队列消息
   * @param message 接收到的消息
   */
  @RabbitListener(id = "handleSimpleQueueMessage", queues = "queue.simple.default")
  public void handleSimpleQueueMessage(Message message) {
    log.info("Received amqp message {} on queue.simple.default", message);
  }

  /**
   * 处理工作队列消息
   * @param message 接收到的消息
   */
  @RabbitListener(id = "handleWorkQueueMessage", queues = "queue.work.default")
  public void handleWorkQueueMessage(Message message) {
    log.info("Received amqp message {} on queue.work.default", message);
  }

  /**
   * 处理发布/订阅模式队列1的消息
   * @param message 接收到的消息
   */
  @RabbitListener(id = "handleDefaultPubSubQueue1", queues = "queue.pubsub.default.1")
  public void handleDefaultPubSubQueue1(Message message) {
    log.info("Received amqp message {} on queue.pubsub.default.1", message);
  }

  /**
   * 处理发布/订阅模式队列2的消息
   * @param message 接收到的消息
   */
  @RabbitListener(id = "handleDefaultPubSubQueue2", queues = "queue.pubsub.default.2")
  public void handleDefaultPubSubQueue2(Message message) {
    log.info("Received amqp message {} on queue.pubsub.default.2", message);
  }

  /**
   * 处理主题模式队列1的消息
   * @param message 接收到的消息
   */
  @RabbitListener(id = "handleDefaultTopicQueue1", queues = "queue.topic.default.1")
  public void handleDefaultTopicQueue1(Message message) {
    log.info("Received amqp message {} on queue.topic.default.1", message);
  }

  /**
   * 处理主题模式队列2的消息
   * @param message 接收到的消息
   */
  @RabbitListener(id = "handleDefaultTopicQueue2", queues = "queue.topic.default.2")
  public void handleDefaultTopicQueue2(Message message) {
    log.info("Received amqp message {} on queue.topic.default.2", message);
  }
}
