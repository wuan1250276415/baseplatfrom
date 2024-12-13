package dev.wuan.wuan.config.security;

import dev.wuan.wuan.dto.urp.UserRolePermissionDto;
import dev.wuan.wuan.service.UserRolePermissionService;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户详情服务实现类
 * 用于加载用户详细信息并构建Spring Security所需的UserDetails对象
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  /** 用户角色权限服务 */
  private final UserRolePermissionService userRolePermissionService;

  /**
   * 根据用户ID加载用户详情
   * @param id 用户ID
   * @return 用户详情对象
   * @throws UsernameNotFoundException 当用户未找到时抛出此异常
   */
  @Override
  public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
    // 查询用户及其角色权限信息
    Optional<UserRolePermissionDto> queryOptional =
        userRolePermissionService.queryUniqueUserWithRolePermission(Long.valueOf(id));
    
    // 如果用户不存在则抛出异常
    UserRolePermissionDto userRolePermissionDto =
        queryOptional.orElseThrow(
            () -> new UsernameNotFoundException(String.format("uid %s user not found", id)));
    
    // 构建并返回UserDetails对象
    return new User(
        userRolePermissionDto.getUsername(),
        userRolePermissionDto.getPassword(),
        userRolePermissionDto.getEnable(), // 账户是否启用
        true, // 账户未过期
        true, // 凭证未过期
        true, // 账户未锁定
        userRolePermissionDto.getPermissions().stream()
            .map((permission) -> new SimpleGrantedAuthority(permission.getCode()))
            .collect(Collectors.toSet())); // 转换权限列表
  }
}
