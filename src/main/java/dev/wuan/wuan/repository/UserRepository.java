package dev.wuan.wuan.repository;

import static org.jooq.generated.wuan.tables.Permission.PERMISSION;
import static org.jooq.generated.wuan.tables.Role.ROLE;
import static org.jooq.generated.wuan.tables.RolePermissionMap.ROLE_PERMISSION_MAP;
import static org.jooq.generated.wuan.tables.User.USER;
import static org.jooq.generated.wuan.tables.UserRoleMap.USER_ROLE_MAP;
import static org.jooq.impl.DSL.*;

import dev.wuan.wuan.dto.PageRequestDto;
import dev.wuan.wuan.dto.urp.PermissionDto;
import dev.wuan.wuan.dto.urp.RoleDto;
import dev.wuan.wuan.dto.urp.UserQueryDto;
import dev.wuan.wuan.dto.urp.UserRolePermissionDto;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.generated.wuan.tables.daos.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户数据访问层
 * 处理用户相关的数据库操作
 */
@Repository
public class UserRepository extends UserDao {

  /**
   * 构造函数
   * @param configuration JOOQ配置
   */
  @Autowired
  public UserRepository(Configuration configuration) {
    super(configuration);
  }

  /**
   * 分页查询用户信息
   * @param pageRequestDto 分页请求参数
   * @param userQueryDto 用户查询条件
   * @return 用户记录结果集
   */
  public Result<Record> pageFetchBy(PageRequestDto pageRequestDto, UserQueryDto userQueryDto) {
    String username = userQueryDto.getUsername();
    Condition whereCondition = username != null ? 
        USER.USERNAME.like("%" + username + "%") : 
        noCondition();
        
    return ctx()
        .select(asterisk(), DSL.count().over().as("total_user"))
        .from(USER)
        .where(whereCondition)
        .orderBy(pageRequestDto.getSortFields())
        .limit(pageRequestDto.getSize())
        .offset(pageRequestDto.getOffset())
        .fetch();
  }

  /**
   * 获取用户及其关联的角色和权限信息(使用嵌套集合方式)
   * @param userId 用户ID
   * @return 用户角色权限DTO
   */
  public UserRolePermissionDto fetchUniqueUserDtoWithNestedRolePermissionBy(Long userId) {
    // 构建权限子查询
    SelectJoinStep<Record> permissionSubQuery = select(PERMISSION.asterisk())
        .from(ROLE_PERMISSION_MAP)
        .leftJoin(PERMISSION)
        .on(ROLE_PERMISSION_MAP.PERMISSION_ID.eq(PERMISSION.ID));

    // 构建角色子查询
    SelectJoinStep<Record> roleSubQuery = select(
        ROLE.asterisk(),
        multiset(
            permissionSubQuery
            .where(ROLE_PERMISSION_MAP.ROLE_ID.eq(ROLE.ID))
            .asTable())  // 将子查询转换为表
            .convertFrom(r -> r.map(record -> record.into(PermissionDto.class)))
            .as("permissions"))
        .from(USER_ROLE_MAP)
        .leftJoin(ROLE)
        .on(USER_ROLE_MAP.ROLE_ID.eq(ROLE.ID));

    return ctx()
        .select(
            USER.asterisk(),
            multiset(
                roleSubQuery
                    .where(USER.ID.eq(USER_ROLE_MAP.USER_ID)))
                .convertFrom(r -> r.map(record -> record.into(RoleDto.class)))
                .as("roles"))
        .from(USER)
        .where(USER.ID.eq(userId))
        .fetchOneInto(UserRolePermissionDto.class);
  }

  /**
   * 获取用户及其关联的角色和权限信息(使用JOIN方式)
   * @param userId 用户ID
   * @return 记录结果集
   */
  public Result<Record> fetchUniqueUserWithRolePermissionBy(Long userId) {
    return ctx()
        .select()
        .from(USER)
        .leftJoin(USER_ROLE_MAP)
        .on(USER.ID.eq(USER_ROLE_MAP.USER_ID))
        .leftJoin(ROLE)
        .on(USER_ROLE_MAP.ROLE_ID.eq(ROLE.ID))
        .leftJoin(ROLE_PERMISSION_MAP)
        .on(ROLE.ID.eq(ROLE_PERMISSION_MAP.ROLE_ID))
        .leftJoin(PERMISSION)
        .on(ROLE_PERMISSION_MAP.PERMISSION_ID.eq(PERMISSION.ID))
        .where(USER.ID.eq(userId))
        .fetch();
  }

  /**
   * 根据用户名删除用户
   * @param username 用户名
   */
  @Transactional
  public void deleteByUsername(String username) {
    ctx().delete(USER)
         .where(USER.USERNAME.eq(username))
         .execute();
  }
}
