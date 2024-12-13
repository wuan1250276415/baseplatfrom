package dev.wuan.wuan.integration.e2e;

import static dev.wuan.wuan.config.WebSocketConfig.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.wuan.wuan.config.security.CookieJwt;
import dev.wuan.wuan.dto.ws.InboundMessage;
import dev.wuan.wuan.dto.ws.OutboundMessage;
import dev.wuan.wuan.repository.UserRepository;
import java.lang.reflect.Type;
import java.util.concurrent.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.generated.wuan.tables.pojos.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Disabled
class WebSocketTest {

  @LocalServerPort private Integer port;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private UserRepository userRepository;
  @Autowired private CookieJwt cookieJwt;
  private final WebSocketStompClient stompClient =
      new WebSocketStompClient(new StandardWebSocketClient());
  ;
  private final BlockingQueue<String> store = new LinkedBlockingDeque<>();
  private static WebSocketHttpHeaders headers;

  private static final String USERNAME = "test_eingxa3e2@gmail.com";
  private static final String PASSWORD = "test_nwdbCAh8hUFmACM9";

  @BeforeEach
  public void authentication() {
    User stubUser = new User();
    stubUser.setUsername(USERNAME);
    stubUser.setPassword(passwordEncoder.encode(PASSWORD));
    userRepository.insert(stubUser);
    User user = userRepository.fetchOneByUsername(USERNAME);
    String jwt = cookieJwt.createJwt(String.valueOf(user.getId()));
    headers = new WebSocketHttpHeaders();
    headers.set("Cookie", String.format("%s=%s", cookieJwt.getCookieName(), jwt));
  }

  @AfterEach
  public void cleanUp() {
    stompClient.stop();
    store.clear();
    userRepository.deleteByUsername(USERNAME);
  }

  @Test
  public void connect_whenConnectServerWithoutAuthenticationCookie_shouldFailed() {
    CompletableFuture<StompSession> stompSessionCompletableFuture =
        stompClient.connectAsync(
            String.format("ws://localhost:%d%s", port, ROOT_PATH),
            new StompSessionHandlerAdapter() {});
    Assertions.assertThrows(ExecutionException.class, stompSessionCompletableFuture::get);
  }

  @Test
  public void connect_whenConnectServerUseStompClient_shouldSuccess() throws Exception {
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    CompletableFuture<StompSession> stompSessionCompletableFuture =
        connectServerAsyncWithAuthHeader();
    StompSession session = stompSessionCompletableFuture.get(2, TimeUnit.SECONDS);
    Assertions.assertTrue(session.isConnected());
  }

  private @NotNull CompletableFuture<StompSession> connectServerAsyncWithAuthHeader() {
    return stompClient.connectAsync(
        String.format("ws://localhost:%d%s", port, ROOT_PATH),
        headers,
        new StompSessionHandlerAdapter() {});
  }

  @Test
  public void topicSubscribe_wendSendMessage2Topic_shouldNoticeSubscriberOnThisTopic()
      throws Exception {
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    CompletableFuture<StompSession> stompSessionCompletableFuture =
        connectServerAsyncWithAuthHeader();
    StompSession session = stompSessionCompletableFuture.get(2, TimeUnit.SECONDS);
    session.subscribe(
        TOPIC,
        new StompFrameHandler() {
          @Override
          public Type getPayloadType(StompHeaders headers) {
            return OutboundMessage.class;
          }

          @Override
          public void handleFrame(StompHeaders headers, Object payload) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
              store.offer(objectMapper.writeValueAsString(payload));
            } catch (JsonProcessingException e) {
              Assertions.fail(e);
            }
          }
        });
    ObjectMapper objectMapper = new ObjectMapper();
    InboundMessage inboundMessage = new InboundMessage();
    inboundMessage.setName("Johnson");
    session.send(RECEIVE_ENDPOINT_PREFIXES + "/entry", inboundMessage);
    Assertions.assertEquals(
        objectMapper.writeValueAsString(
            new OutboundMessage(String.format("Greetings %s", "Johnson"))),
        store.poll(2, TimeUnit.SECONDS));
  }
}
