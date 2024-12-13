package dev.wuan.wuan.repository;

import static org.jooq.generated.wuan.tables.UserRoleMap.USER_ROLE_MAP;

import org.jooq.Configuration;
import org.jooq.generated.wuan.tables.daos.UserRoleMapDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户角色映射仓储类
 * 处理用户与角色之间的关联关系
 */
@Repository
public class UserRoleMapRepository extends UserRoleMapDao {

  /**
   * 构造函数
   * @param configuration JOOQ配置
   */
  @Autowired
  public UserRoleMapRepository(Configuration configuration) {
    super(configuration);
  }

  /**
   * 根据用户ID删除用户角色映射关系
   * @param userId 用户ID
   */
  @Transactional
  public void deleteByUserId(Long userId) {
    if (userId == null) {
      throw new IllegalArgumentException("用户ID不能为空");
    }
    ctx().deleteFrom(USER_ROLE_MAP)
         .where(USER_ROLE_MAP.USER_ID.eq(userId))
         .execute();
  }
}
