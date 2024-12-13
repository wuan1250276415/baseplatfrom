package dev.wuan.wuan.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.wuan.wuan.dto.amqp.Message;
import jakarta.annotation.Resource;

/**
 * RabbitMQ消息生产者控制器
 * 用于测试不同的消息队列模式
 */
@RestController
@RequestMapping(value = "/amqp/publisher/")
public class AmqpMessageProducer {

  /** 成功响应消息 */
  public static final String SUCCEED = "succeed";

  /** RabbitMQ模板类,用于发送消息 */
  @Resource 
  private RabbitTemplate rabbitTemplate;

  /**
   * 测试简单模式
   * 一个生产者对应一个消费者
   * @param routingKey 路由键
   * @param message 消息内容
   * @return 发送结果
   */
  @PostMapping(value = "/test/simple-mode")
  public String testSimpleMode(
      @RequestParam(name = "routing-key") String routingKey, 
      @RequestBody Message message) {
    rabbitTemplate.convertAndSend("exchange.direct.default", routingKey, message);
    return SUCCEED;
  }

  /**
   * 测试工作队列模式
   * 一个生产者对应多个消费者,消息只会被一个消费者处理
   * @param routingKey 路由键
   * @param message 消息内容
   * @return 发送结果
   */
  @PostMapping(value = "/test/work-mode")
  public String testWorkMode(
      @RequestParam(name = "routing-key") String routingKey, 
      @RequestBody Message message) {
    rabbitTemplate.convertAndSend("exchange.direct.default", routingKey, message);
    return SUCCEED;
  }

  /**
   * 测试主题模式
   * 根据routing key的模式匹配发送到不同的队列
   * @param routingKey 路由键
   * @param message 消息内容
   * @return 发送结果
   */
  @PostMapping(value = "/test/topic-mode")
  public String testTopicMode(
      @RequestParam(name = "routing-key") String routingKey, 
      @RequestBody Message message) {
    rabbitTemplate.convertAndSend("exchange.topic.default", routingKey, message);
    return SUCCEED;
  }

  /**
   * 测试发布订阅模式
   * 消息会广播到所有绑定的队列
   * @param message 消息内容
   * @return 发送结果
   */
  @PostMapping(value = "/test/pubsub-mode")
  public String testPubSubMode(@RequestBody Message message) {
    rabbitTemplate.convertAndSend("exchange.pubsub.default", "", message);
    return SUCCEED;
  }
}
