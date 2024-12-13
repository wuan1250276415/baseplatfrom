package dev.wuan.wuan.repository;

import static org.jooq.generated.wuan.tables.Permission.PERMISSION;
import static org.jooq.generated.wuan.tables.Role.ROLE;
import static org.jooq.generated.wuan.tables.RolePermissionMap.ROLE_PERMISSION_MAP;
import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.noCondition;

import dev.wuan.wuan.dto.PageRequestDto;
import dev.wuan.wuan.dto.urp.RoleQueryDto;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.generated.wuan.tables.daos.RoleDao;
import org.jooq.generated.wuan.tables.pojos.Role;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 角色数据访问层
 */
@Repository
public class RoleRepository extends RoleDao {

  @Autowired
  public RoleRepository(Configuration configuration) {
    super(configuration);
  }

  /**
   * 根据角色编码列表查询角色信息
   *
   * @param roleCodeList 角色编码列表
   * @return 角色列表
   */
  public List<Role> selectByRoleCodeIn(List<String> roleCodeList) {
    return ctx()
        .selectFrom(ROLE)
        .where(ROLE.CODE.in(roleCodeList))
        .fetchInto(Role.class);
  }

  /**
   * 根据角色ID列表查询角色信息
   *
   * @param roleIdList 角色ID列表
   * @return 角色列表
   */
  public List<Role> selectByRoleIdIn(List<Long> roleIdList) {
    return ctx()
        .selectFrom(ROLE)
        .where(ROLE.ID.in(roleIdList))
        .fetchInto(Role.class);
  }

  /**
   * 分页查询角色信息
   *
   * @param pageRequestDto 分页请求参数
   * @param roleQueryDto 角色查询条件
   * @return 角色记录结果集
   */
  public Result<Record> pageFetchBy(PageRequestDto pageRequestDto, RoleQueryDto roleQueryDto) {
    return ctx()
        .select(asterisk(), DSL.count(ROLE.ID).over().as("total_role"))
        .from(ROLE)
        .where(buildRoleIdListCondition(roleQueryDto))
        .and(buildRoleIdCondition(roleQueryDto))
        .and(buildRoleNameCondition(roleQueryDto))
        .and(buildRoleCodeCondition(roleQueryDto))
        .orderBy(pageRequestDto.getSortFields())
        .limit(pageRequestDto.getSize())
        .offset(pageRequestDto.getOffset())
        .fetch();
  }

  /**
   * 查询指定角色ID的角色及其权限信息
   *
   * @param roleId 角色ID
   * @return 角色及权限记录结果集
   */
  public Result<Record> fetchUniqueRoleWithPermission(Long roleId) {
    return ctx()
        .select(asterisk())
        .from(ROLE)
        .leftJoin(ROLE_PERMISSION_MAP)
        .on(ROLE.ID.eq(ROLE_PERMISSION_MAP.ROLE_ID))
        .leftJoin(PERMISSION)
        .on(ROLE_PERMISSION_MAP.PERMISSION_ID.eq(PERMISSION.ID))
        .where(ROLE.ID.eq(roleId))
        .orderBy(ROLE.ID)
        .fetch();
  }

  // 以下是私有辅助方法，用于构建查询条件

  private org.jooq.Condition buildRoleIdListCondition(RoleQueryDto roleQueryDto) {
    return CollectionUtils.isEmpty(roleQueryDto.getRoleIdList())
        ? noCondition()
        : ROLE.ID.in(roleQueryDto.getRoleIdList());
  }

  private org.jooq.Condition buildRoleIdCondition(RoleQueryDto roleQueryDto) {
    return roleQueryDto.getRoleId() == null
        ? noCondition()
        : ROLE.ID.eq(roleQueryDto.getRoleId());
  }

  private org.jooq.Condition buildRoleNameCondition(RoleQueryDto roleQueryDto) {
    return StringUtils.isEmpty(roleQueryDto.getRoleName())
        ? noCondition()
        : ROLE.NAME.like("%" + roleQueryDto.getRoleName() + "%");
  }

  private org.jooq.Condition buildRoleCodeCondition(RoleQueryDto roleQueryDto) {
    return StringUtils.isEmpty(roleQueryDto.getRoleCode())
        ? noCondition()
        : ROLE.CODE.eq(roleQueryDto.getRoleCode());
  }
}
