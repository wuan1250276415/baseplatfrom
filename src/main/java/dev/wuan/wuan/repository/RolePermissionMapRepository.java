package dev.wuan.wuan.repository;

import static org.jooq.generated.wuan.tables.RolePermissionMap.ROLE_PERMISSION_MAP;

import org.jooq.Configuration;
import org.jooq.generated.wuan.tables.daos.RolePermissionMapDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色权限映射关系数据访问层
 */
@Repository
public class RolePermissionMapRepository extends RolePermissionMapDao {

  /**
   * 构造函数
   * @param configuration JOOQ配置
   */
  @Autowired
  public RolePermissionMapRepository(Configuration configuration) {
    super(configuration);
  }

  /**
   * 根据角色ID删除对应的角色-权限映射记录
   * @param roleId 角色ID
   */
  @Transactional
  public void deleteByRoleId(Long roleId) {
    ctx()
        .deleteFrom(ROLE_PERMISSION_MAP)
        .where(ROLE_PERMISSION_MAP.ROLE_ID.eq(roleId))
        .execute();
  }
}
