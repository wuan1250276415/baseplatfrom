package dev.wuan.wuan.controller;

import dev.wuan.wuan.dto.PageRequestDto;
import dev.wuan.wuan.dto.PageResponseDto;
import dev.wuan.wuan.dto.urp.*;
import dev.wuan.wuan.service.UserRolePermissionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户角色权限控制器
 * 处理用户、角色、权限的查询和绑定等操作
 */
@RestController
@RequestMapping("/urp")
@RequiredArgsConstructor
public class UserRolePermissionController {

  /** 用户角色权限服务接口 */
  private final UserRolePermissionService userRolePermissionService;

  /**
   * 分页查询用户信息
   * @param pageRequestDto 分页请求参数
   * @param userQueryDto 用户查询条件
   * @return 分页用户信息列表
   */
  @PreAuthorize("hasAuthority(T(dev.wuan.wuan.model.urp.EPermission).READ_USER_ROLE_PERMISSION)")
  @GetMapping("/user")
  @ResponseStatus(HttpStatus.OK)
  PageResponseDto<List<UserRolePermissionDto>> queryUser(
      @ModelAttribute PageRequestDto pageRequestDto, 
      @ModelAttribute UserQueryDto userQueryDto) {
    return userRolePermissionService.pageQueryUser(pageRequestDto, userQueryDto);
  }

  /**
   * 分页查询角色信息
   * @param pageRequestDto 分页请求参数
   * @param roleQueryDto 角色查询条件
   * @return 分页角色信息列表
   */
  @PreAuthorize("hasAuthority(T(dev.wuan.wuan.model.urp.EPermission).READ_USER_ROLE_PERMISSION)")
  @GetMapping("/role")
  @ResponseStatus(HttpStatus.OK)
  PageResponseDto<List<RoleDto>> queryRole(
      @ModelAttribute PageRequestDto pageRequestDto, 
      @ModelAttribute RoleQueryDto roleQueryDto) {
    return userRolePermissionService.pageQueryRole(pageRequestDto, roleQueryDto);
  }

  /**
   * 分页查询权限信息
   * @param pageRequestDto 分页请求参数
   * @param permissionQueryDto 权限查询条件
   * @return 分页权限信息列表
   */
  @PreAuthorize("hasAuthority(T(dev.wuan.wuan.model.urp.EPermission).READ_USER_ROLE_PERMISSION)")
  @GetMapping("/permission")
  @ResponseStatus(HttpStatus.OK)
  PageResponseDto<List<PermissionDto>> queryPermission(
      @ModelAttribute PageRequestDto pageRequestDto,
      @ModelAttribute PermissionQueryDto permissionQueryDto) {
    return userRolePermissionService.pageQueryPermission(pageRequestDto, permissionQueryDto);
  }

  /**
   * 为用户绑定角色
   * @param userId 用户ID
   * @param roleIdList 角色ID列表
   */
  @PreAuthorize("hasAuthority(T(dev.wuan.wuan.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @PostMapping("/user/{userId}/bind-role")
  @ResponseStatus(HttpStatus.OK)
  void bindRoleToUser(
      @PathVariable Long userId, 
      @RequestBody List<Long> roleIdList) {
    userRolePermissionService.bindRoleToUser(userId, roleIdList);
  }

  /**
   * 为角色绑定权限
   * @param roleId 角色ID
   * @param permissionIdList 权限ID列表
   */
  @PreAuthorize("hasAuthority(T(dev.wuan.wuan.model.urp.EPermission).WRITE_USER_ROLE_PERMISSION)")
  @PostMapping("/role/{roleId}/bind-permission")
  @ResponseStatus(HttpStatus.OK)
  void bindPermissionToRole(
      @PathVariable Long roleId, 
      @RequestBody List<Long> permissionIdList) {
    userRolePermissionService.bindPermissionToRole(roleId, permissionIdList);
  }
}
