package dev.wuan.wuan.job;

import java.text.MessageFormat;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * 数据备份定时任务
 * 用于定期备份系统数据,确保数据安全
 */
@Slf4j
public class DataBackupJob implements Job {

  /**
   * 执行备份任务
   * @param context 任务执行上下文,包含任务相关参数
   */
  @Override
  public void execute(JobExecutionContext context) {
    // 从上下文中获取用户ID
    String userId = context.getJobDetail().getJobDataMap().getString("userId");
    
    // 记录任务执行信息
    log.info(
        MessageFormat.format(
            "开始执行备份任务: 任务名称={0}, 用户ID={1}, 执行线程={2}",
            getClass().getSimpleName(), 
            userId,
            Thread.currentThread().getName())
    );
  }
}
