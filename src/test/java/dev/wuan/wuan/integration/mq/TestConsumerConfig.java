package dev.wuan.wuan.integration.mq;

import dev.wuan.wuan.component.AmqpMessageConsumer;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.context.annotation.Bean;

@RabbitListenerTest
public class TestConsumerConfig {

  @Bean
  public AmqpMessageConsumer amqpMessageTestConsumer() {
    return new AmqpMessageConsumer();
  }
}
