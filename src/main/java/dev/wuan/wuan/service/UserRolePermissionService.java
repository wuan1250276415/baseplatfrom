package dev.wuan.wuan.service;

import static org.jooq.generated.wuan.tables.Permission.PERMISSION;
import static org.jooq.generated.wuan.tables.Role.ROLE;
import static org.jooq.generated.wuan.tables.User.USER;

import dev.wuan.wuan.dto.PageRequestDto;
import dev.wuan.wuan.dto.PageResponseDto;
import dev.wuan.wuan.dto.urp.*;
import dev.wuan.wuan.exception.BusinessException;
import dev.wuan.wuan.model.urp.ERole;
import dev.wuan.wuan.repository.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.generated.wuan.tables.pojos.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户角色权限服务类
 * 处理用户、角色、权限之间的关系管理
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserRolePermissionService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final UserRoleMapRepository userRoleMapRepository;
  private final PermissionRepository permissionRepository;
  private final RolePermissionMapRepository rolePermissionMapRepository;

  /**
   * 分页查询用户信息及其关联的角色和权限
   * @param pageRequestDto 分页请求参数
   * @param userQueryDto 用户查询条件
   * @return 分页响应数据
   */
  public PageResponseDto<List<UserRolePermissionDto>> pageQueryUser(
      PageRequestDto pageRequestDto, UserQueryDto userQueryDto) {
    Result<Record> userRecords = userRepository.pageFetchBy(pageRequestDto, userQueryDto);
    if (userRecords.isEmpty()) {
      return PageResponseDto.empty();
    }
    List<UserRolePermissionDto> userRolePermissionDtoList = userRecords.stream()
        .map(record -> queryUniqueUserWithRolePermission(record.getValue(USER.ID)).orElseThrow())
        .toList();
    return new PageResponseDto<>(
        userRecords.get(0).getValue("total_user", Integer.class), userRolePermissionDtoList);
  }

  /**
   * 查询单个用户的角色和权限信息
   * @param userId 用户ID
   * @return 用户角色权限信息
   */
  public Optional<UserRolePermissionDto> queryUniqueUserWithRolePermission(Long userId) {
    UserRolePermissionDto records = userRepository.fetchUniqueUserDtoWithNestedRolePermissionBy(userId);
    return Optional.ofNullable(records);
  }

  /**
   * 分页查询角色信息
   * @param pageRequestDto 分页请求参数
   * @param roleQueryDto 角色查询条件
   * @return 分页响应数据
   */
  public PageResponseDto<List<RoleDto>> pageQueryRole(
      PageRequestDto pageRequestDto, RoleQueryDto roleQueryDto) {
    if (roleQueryDto.getUserId() != null) {
      List<Long> roleIdList = userRoleMapRepository.fetchByUserId(roleQueryDto.getUserId()).stream()
          .map(UserRoleMap::getRoleId)
          .toList();
      if (roleIdList.isEmpty()) {
        return PageResponseDto.empty();
      }
      roleQueryDto.setRoleIdList(roleIdList);
    }
    
    Result<Record> roleRecords = roleRepository.pageFetchBy(pageRequestDto, roleQueryDto);
    if (roleRecords.isEmpty()) {
      return PageResponseDto.empty();
    }
    
    List<RoleDto> roleDtoList = roleRecords.stream()
        .map(record -> queryUniqueRoleWithPermission(record.getValue(ROLE.ID)).orElseThrow())
        .toList();
    return new PageResponseDto<>(
        roleRecords.get(0).getValue("total_role", Integer.class), roleDtoList);
  }

  /**
   * 查询单个角色及其权限信息
   * @param roleId 角色ID
   * @return 角色权限信息
   */
  public Optional<RoleDto> queryUniqueRoleWithPermission(Long roleId) {
    Result<Record> roleWithPermissionRecords = roleRepository.fetchUniqueRoleWithPermission(roleId);
    if (roleWithPermissionRecords.isEmpty()) {
      return Optional.empty();
    }
    RoleDto roleDto = createRbacDtoRolePart(roleWithPermissionRecords);
    setCurrentRolePermission(roleDto, roleWithPermissionRecords);
    return Optional.of(roleDto);
  }

  /**
   * 分页查询权限信息
   * @param pageRequestDto 分页请求参数
   * @param permissionQueryDto 权限查询条件
   * @return 分页响应数据
   */
  public PageResponseDto<List<PermissionDto>> pageQueryPermission(
      PageRequestDto pageRequestDto, PermissionQueryDto permissionQueryDto) {
    if (permissionQueryDto.getRoleId() != null) {
      List<Long> permissionIdList = rolePermissionMapRepository.fetchByRoleId(permissionQueryDto.getRoleId()).stream()
          .map(RolePermissionMap::getPermissionId)
          .toList();
      if (permissionIdList.isEmpty()) {
        return PageResponseDto.empty();
      }
      permissionQueryDto.setPermissionIdList(permissionIdList);
    }
    
    Result<Record> permissionRecords = permissionRepository.pageFetchBy(pageRequestDto, permissionQueryDto);
    if (permissionRecords.isEmpty()) {
      return PageResponseDto.empty();
    }
    
    List<PermissionDto> permissionDtoList = permissionRecords.into(Permission.class).stream()
        .map(pojo -> new PermissionDto(pojo.getId(), pojo.getName(), pojo.getCode()))
        .toList();
    return new PageResponseDto<>(
        permissionRecords.get(0).getValue("total_permission", Integer.class), permissionDtoList);
  }

  /**
   * 为角色绑定权限
   * @param roleId 角色ID
   * @param permissionIdList 权限ID列表
   */
  @Transactional(rollbackFor = Throwable.class)
  public void bindPermissionToRole(Long roleId, List<Long> permissionIdList) {
    rolePermissionMapRepository.deleteByRoleId(roleId);
    if (CollectionUtils.isEmpty(permissionIdList)) {
      return;
    }
    
    List<Permission> permissions = permissionRepository.selectByPermissionIdIn(permissionIdList);
    if (CollectionUtils.isEmpty(permissions)) {
      throw new BusinessException("绑定的权限不存在");
    }
    
    List<RolePermissionMap> permissionMapList = permissions.stream()
        .map(permission -> {
          RolePermissionMap rolePermissionMap = new RolePermissionMap();
          rolePermissionMap.setRoleId(roleId);
          rolePermissionMap.setPermissionId(permission.getId());
          return rolePermissionMap;
        })
        .collect(Collectors.toList());
    rolePermissionMapRepository.insert(permissionMapList);
  }

  /**
   * 为用户绑定角色
   * @param userId 用户ID
   * @param roleIdList 角色ID列表
   */
  @Transactional(rollbackFor = Throwable.class)
  public void bindRoleToUser(Long userId, List<Long> roleIdList) {
    userRoleMapRepository.deleteByUserId(userId);
    if (CollectionUtils.isEmpty(roleIdList)) {
      return;
    }
    
    List<Role> roles = roleRepository.selectByRoleIdIn(roleIdList);
    if (CollectionUtils.isEmpty(roles)) {
      throw new BusinessException("绑定的角色不存在");
    }
    
    List<UserRoleMap> userRoleMapList = roles.stream()
        .map(role -> {
          UserRoleMap userRoleMap = new UserRoleMap();
          userRoleMap.setUserId(userId);
          userRoleMap.setRoleId(role.getId());
          return userRoleMap;
        })
        .collect(Collectors.toList());
    userRoleMapRepository.insert(userRoleMapList);
  }

  /**
   * 为用户绑定角色模块
   * @param userId 用户ID
   * @param eRoleList 角色枚举列表
   */
  @Transactional(rollbackFor = Throwable.class)
  public void bindRoleModuleToUser(Long userId, List<ERole> eRoleList) {
    bindRoleToUser(
        userId,
        roleRepository.selectByRoleCodeIn(eRoleList.stream().map(Enum::name).collect(Collectors.toList()))
            .stream()
            .map(Role::getId)
            .toList());
  }

  /**
   * 设置当前角色的权限信息
   */
  private void setCurrentRolePermission(RoleDto roleDto, List<Record> roleResult) {
    if (roleResult.get(0).getValue(PERMISSION.ID) != null) {
      roleResult.forEach(record -> {
        PermissionDto permissionDto = createRbacDtoPermissionPart(record);
        roleDto.getPermissions().add(permissionDto);
      });
    }
  }

  /**
   * 创建权限DTO对象
   */
  private PermissionDto createRbacDtoPermissionPart(Record record) {
    PermissionDto permissionDto = new PermissionDto();
    permissionDto.setId(record.getValue(PERMISSION.ID));
    permissionDto.setCode(record.getValue(PERMISSION.CODE));
    permissionDto.setName(record.getValue(PERMISSION.NAME));
    return permissionDto;
  }

  /**
   * 创建角色DTO对象
   */
  private RoleDto createRbacDtoRolePart(List<Record> roleResult) {
    RoleDto roleDto = new RoleDto();
    roleDto.setId(roleResult.get(0).getValue(ROLE.ID));
    roleDto.setCode(roleResult.get(0).getValue(ROLE.CODE));
    roleDto.setName(roleResult.get(0).getValue(ROLE.NAME));
    return roleDto;
  }
}
