package dev.wuan.wuan.controller;

import static dev.wuan.wuan.config.WebSocketConfig.*;

import dev.wuan.wuan.dto.ws.InboundMessage;
import dev.wuan.wuan.dto.ws.OutboundMessage;
import jakarta.annotation.Resource;
import java.security.Principal;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

/**
 * WebSocket STOMP 控制器
 * 处理WebSocket相关的消息收发
 */
@RestController
public class WebSocketStompController {

  /** 消息模板,用于发送消息 */
  @Resource 
  private SimpMessagingTemplate simpMessagingTemplate;

  /**
   * 处理用户进入聊天室的消息
   * @param inboundMessage 入站消息
   * @return 广播给所有用户的欢迎消息
   */
  @MessageMapping("/entry")
  @SendTo(TOPIC)
  public OutboundMessage enterChatRoom(InboundMessage inboundMessage) {
    String welcomeMsg = String.format("Greetings %s", inboundMessage.getName());
    return new OutboundMessage(welcomeMsg);
  }

  /**
   * 处理私聊消息
   * @param msg 入站消息
   * @param user 当前用户
   */
  @MessageMapping("/chat")
  public void handlePrivateChat(InboundMessage msg, Principal user) {
    String privateMsg = String.format("Hi, %s", msg.getName());
    simpMessagingTemplate.convertAndSendToUser(
        user.getName(), 
        QUEUE, 
        privateMsg
    );
  }
}
