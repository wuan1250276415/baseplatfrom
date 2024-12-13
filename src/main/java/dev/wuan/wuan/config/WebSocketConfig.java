package dev.wuan.wuan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket配置类
 * 用于配置WebSocket消息代理和STOMP端点
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  /** 允许的源域名,用于CORS配置 */
  @Value("${cors.allowedOrigins}")
  private String allowedOrigins;

  /** WebSocket根路径 */
  public static final String ROOT_PATH = "/chat-room";
  /** 广播消息主题前缀 */
  public static final String TOPIC = "/notice";
  /** 点对点消息队列前缀 */
  public static final String QUEUE = "/message";
  /** 客户端发送消息的端点前缀 */
  public static final String RECEIVE_ENDPOINT_PREFIXES = "/app";
  /** 用户相关消息的前缀 */
  public static final String USER_PREFIXES = "/user";

  /**
   * 配置消息代理
   * 设置消息代理的主题前缀和应用程序前缀
   * @param config 消息代理注册表
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    // 启用简单消息代理,支持广播和点对点消息
    config.enableSimpleBroker(TOPIC, QUEUE);
    // 设置客户端发送消息的前缀
    config.setApplicationDestinationPrefixes(RECEIVE_ENDPOINT_PREFIXES);
    // 设置用户消息前缀
    config.setUserDestinationPrefix(USER_PREFIXES);
  }

  /**
   * 注册STOMP端点
   * 配置WebSocket连接端点
   * @param registry STOMP端点注册表
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // 添加WebSocket端点并配置允许的源
    registry.addEndpoint(ROOT_PATH).setAllowedOrigins(allowedOrigins);
  }
}
