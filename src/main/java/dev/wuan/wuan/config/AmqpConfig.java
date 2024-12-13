package dev.wuan.wuan.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AMQP配置类
 * 用于配置RabbitMQ的交换机、队列、绑定关系等
 */
@Configuration
public class AmqpConfig {

  /**
   * 配置默认直连交换机
   * @return DirectExchange实例
   */
  @Bean
  DirectExchange defaultDirectExchange() {
    return new DirectExchange("exchange.direct.default");
  }

  /**
   * 配置默认主题交换机
   * @return TopicExchange实例
   */
  @Bean
  TopicExchange defaultTopicExchange() {
    return new TopicExchange("exchange.topic.default");
  }

  /**
   * 配置默认发布/订阅交换机
   * @return FanoutExchange实例
   */
  @Bean
  FanoutExchange defaultPubSubExchange() {
    return new FanoutExchange("exchange.pubsub.default");
  }

  /**
   * 配置主题模式队列1
   * @return Queue实例
   */
  @Bean
  Queue defaultTopicQueue1() {
    return new Queue("queue.topic.default.1");
  }

  /**
   * 配置主题模式队列2
   * @return Queue实例
   */
  @Bean
  Queue defaultTopicQueue2() {
    return new Queue("queue.topic.default.2");
  }

  /**
   * 配置发布/订阅模式队列1
   * @return Queue实例
   */
  @Bean
  Queue defaultPubSubQueue1() {
    return new Queue("queue.pubsub.default.1");
  }

  /**
   * 配置发布/订阅模式队列2
   * @return Queue实例
   */
  @Bean
  Queue defaultPubSubQueue2() {
    return new Queue("queue.pubsub.default.2");
  }

  /**
   * 配置简单模式队列
   * @return Queue实例
   */
  @Bean
  Queue defaultSimpleQueue() {
    return new Queue("queue.simple.default");
  }

  /**
   * 配置工作模式队列
   * @return Queue实例
   */
  @Bean
  Queue defaultWorkQueue() {
    return new Queue("queue.work.default");
  }

  /**
   * 配置简单模式绑定关系
   * @param defaultSimpleQueue 简单模式队列
   * @param defaultDirectExchange 直连交换机
   * @return Binding实例
   */
  @Bean
  Binding defaultSimpleModeBinding(Queue defaultSimpleQueue, DirectExchange defaultDirectExchange) {
    return BindingBuilder.bind(defaultSimpleQueue)
        .to(defaultDirectExchange)
        .with("baz.simple");
  }

  /**
   * 配置工作模式绑定关系
   * @param defaultWorkQueue 工作模式队列
   * @param defaultDirectExchange 直连交换机
   * @return Binding实例
   */
  @Bean
  Binding defaultWorkModeBinding(Queue defaultWorkQueue, DirectExchange defaultDirectExchange) {
    return BindingBuilder.bind(defaultWorkQueue)
        .to(defaultDirectExchange)
        .with("baz.work");
  }

  /**
   * 配置主题模式绑定关系1
   * @param defaultTopicQueue1 主题模式队列1
   * @param defaultTopicExchange 主题交换机
   * @return Binding实例
   */
  @Bean
  Binding defaultTopicModeBinding(Queue defaultTopicQueue1, TopicExchange defaultTopicExchange) {
    return BindingBuilder.bind(defaultTopicQueue1)
        .to(defaultTopicExchange)
        .with("*.bar.*");
  }

  /**
   * 配置主题模式绑定关系2
   * @param defaultTopicQueue2 主题模式队列2
   * @param defaultTopicExchange 主题交换机
   * @return Binding实例
   */
  @Bean
  Binding defaultTopicModeBinding2(Queue defaultTopicQueue2, TopicExchange defaultTopicExchange) {
    return BindingBuilder.bind(defaultTopicQueue2)
        .to(defaultTopicExchange)
        .with("foo.#");
  }

  /**
   * 配置发布/订阅模式绑定关系1
   * @param defaultPubSubQueue1 发布/订阅模式队列1
   * @param defaultPubSubExchange 发布/订阅交换机
   * @return Binding实例
   */
  @Bean
  Binding defaultPubSubModeBinding(
      Queue defaultPubSubQueue1, FanoutExchange defaultPubSubExchange) {
    return BindingBuilder.bind(defaultPubSubQueue1)
        .to(defaultPubSubExchange);
  }

  /**
   * 配置发布/订阅模式绑定关系2
   * @param defaultPubSubQueue2 发布/订阅模式队列2
   * @param defaultPubSubExchange 发布/订阅交换机
   * @return Binding实例
   */
  @Bean
  Binding defaultPubSubModeBinding2(
      Queue defaultPubSubQueue2, FanoutExchange defaultPubSubExchange) {
    return BindingBuilder.bind(defaultPubSubQueue2)
        .to(defaultPubSubExchange);
  }

  /**
   * 配置消息转换器
   * 使用Jackson2JsonMessageConverter进行消息的JSON序列化和反序列化
   * @return MessageConverter实例
   */
  @Bean
  public MessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
