package dev.wuan.wuan.controller;

import dev.wuan.wuan.config.security.CookieJwt;
import dev.wuan.wuan.dto.sign.SignInDto;
import dev.wuan.wuan.dto.sign.SignUpDto;
import dev.wuan.wuan.service.SignService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证控制器
 * 处理用户登录、注册和登出等认证相关请求
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SignController {

  /** 认证服务接口 */
  private final SignService signService;

  /** Cookie JWT工具类 */
  private final CookieJwt cookieJwt;

  /**
   * 用户登录
   * @param request HTTP请求
   * @param response HTTP响应
   * @param signInDto 登录信息DTO
   */
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/sign-in")
  void signIn(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody @Valid SignInDto signInDto) {
    String userId = String.valueOf(signService.signIn(signInDto));
    cookieJwt.createJwtCookie(request, response, userId);
  }

  /**
   * 用户注册
   * @param signUpDto 注册信息DTO
   */
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/sign-up")
  void signUp(@RequestBody @Valid SignUpDto signUpDto) {
    signService.signUp(signUpDto);
  }

  /**
   * 用户登出
   * @param request HTTP请求
   * @param response HTTP响应
   */
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/sign-out")
  void signOut(HttpServletRequest request, HttpServletResponse response) {
    cookieJwt.removeJwtCookie(request, response);
  }
}
