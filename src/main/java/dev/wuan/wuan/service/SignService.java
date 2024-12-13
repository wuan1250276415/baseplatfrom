package dev.wuan.wuan.service;

import dev.wuan.wuan.dto.sign.SignInDto;
import dev.wuan.wuan.dto.sign.SignUpDto;
import dev.wuan.wuan.exception.BusinessException;
import dev.wuan.wuan.model.urp.ERole;
import dev.wuan.wuan.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.generated.wuan.tables.pojos.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户登录注册服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SignService {

  /** 用户数据访问层 */
  private final UserRepository userRepository;

  /** 密码加密器 */
  private final PasswordEncoder passwordEncoder;

  /** 用户角色权限服务 */
  private final UserRolePermissionService userRolePermissionService;

  /**
   * 用户登录
   *
   * @param signInDto 登录信息传输对象
   * @return 用户ID
   * @throws BusinessException 当用户不存在或密码错误时抛出
   */
  public Long signIn(SignInDto signInDto) {
    // 根据用户名查询用户
    User user = userRepository.fetchOneByUsername(signInDto.getUsername());
    if (user == null) {
      log.warn("用户{}不存在", signInDto.getUsername());
      throw new BusinessException(String.format("%s user not found", signInDto.getUsername()));
    }
    
    // 验证密码
    if (!passwordEncoder.matches(signInDto.getPassword(), user.getPassword())) {
      log.warn("用户{}密码错误", signInDto.getUsername());
      throw new BusinessException("password invalid");
    }
    
    log.info("用户{}登录成功", signInDto.getUsername());
    return user.getId();
  }

  /**
   * 用户注册
   *
   * @param signUpDto 注册信息传输对象
   * @throws BusinessException 当用户名已存在时抛出
   */
  @Transactional(rollbackFor = Throwable.class)
  public void signUp(SignUpDto signUpDto) {
    // 检查用户名是否重复
    if (isUsernameDuplicate(signUpDto.getUsername())) {
      log.warn("用户名{}已存在", signUpDto.getUsername());
      throw new BusinessException(
          String.format("username %s already exist", signUpDto.getUsername()));
    }

    // 创建新用户
    User user = new User();
    user.setUsername(signUpDto.getUsername());
    user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
    userRepository.insert(user);

    // 绑定用户角色
    User insertUser = userRepository.fetchOneByUsername(signUpDto.getUsername());
    userRolePermissionService.bindRoleModuleToUser(insertUser.getId(), List.of(ERole.GENERAL));
    
    log.info("用户{}注册成功", signUpDto.getUsername());
  }

  /**
   * 检查用户名是否重复
   *
   * @param username 用户名
   * @return true表示重复，false表示不重复
   */
  public boolean isUsernameDuplicate(String username) {
    return userRepository.fetchOneByUsername(username) != null;
  }
}
