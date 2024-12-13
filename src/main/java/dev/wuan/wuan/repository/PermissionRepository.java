package dev.wuan.wuan.repository;

import static org.jooq.generated.wuan.tables.Permission.PERMISSION;
import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.noCondition;

import dev.wuan.wuan.dto.PageRequestDto;
import dev.wuan.wuan.dto.urp.PermissionQueryDto;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.generated.wuan.tables.daos.PermissionDao;
import org.jooq.generated.wuan.tables.pojos.Permission;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 权限数据访问层
 * 用于处理权限相关的数据库操作
 */
@Repository
public class PermissionRepository extends PermissionDao {

  /**
   * 构造函数
   * @param configuration JOOQ配置
   */
  @Autowired
  public PermissionRepository(Configuration configuration) {
    super(configuration);
  }

  /**
   * 分页查询权限信息
   * @param pageRequestDto 分页请求参数
   * @param permissionQueryDto 权限查询条件
   * @return 权限记录结果集
   */
  public Result<Record> pageFetchBy(
      PageRequestDto pageRequestDto, PermissionQueryDto permissionQueryDto) {
    return ctx()
        .select(asterisk(), DSL.count().over().as("total_permission"))
        .from(PERMISSION)
        .where(buildPermissionIdListCondition(permissionQueryDto.getPermissionIdList()))
        .and(buildPermissionIdCondition(permissionQueryDto.getPermissionId()))
        .and(buildPermissionNameCondition(permissionQueryDto.getPermissionName()))
        .and(buildPermissionCodeCondition(permissionQueryDto.getPermissionName(), 
            permissionQueryDto.getPermissionCode()))
        .orderBy(pageRequestDto.getSortFields())
        .limit(pageRequestDto.getSize())
        .offset(pageRequestDto.getOffset())
        .fetch();
  }

  /**
   * 根据权限ID列表查询权限信息
   * @param permissionIdList 权限ID列表
   * @return 权限信息列表
   */
  public List<Permission> selectByPermissionIdIn(List<Long> permissionIdList) {
    return ctx()
        .selectFrom(PERMISSION)
        .where(PERMISSION.ID.in(permissionIdList))
        .fetchInto(Permission.class);
  }

  /**
   * 构建权限ID列表查询条件
   */
  private org.jooq.Condition buildPermissionIdListCondition(List<Long> permissionIdList) {
    return CollectionUtils.isEmpty(permissionIdList) 
        ? noCondition() 
        : PERMISSION.ID.in(permissionIdList);
  }

  /**
   * 构建权限ID查询条件
   */
  private org.jooq.Condition buildPermissionIdCondition(Long permissionId) {
    return permissionId == null 
        ? noCondition() 
        : PERMISSION.ID.eq(permissionId);
  }

  /**
   * 构建权限名称查询条件
   */
  private org.jooq.Condition buildPermissionNameCondition(String permissionName) {
    return StringUtils.isEmpty(permissionName)
        ? noCondition()
        : PERMISSION.NAME.like("%" + permissionName + "%");
  }

  /**
   * 构建权限编码查询条件
   */
  private org.jooq.Condition buildPermissionCodeCondition(String permissionName, String permissionCode) {
    return StringUtils.isEmpty(permissionName)
        ? noCondition()
        : PERMISSION.CODE.eq(permissionCode);
  }
}
