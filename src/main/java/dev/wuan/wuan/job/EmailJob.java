package dev.wuan.wuan.job;

import java.text.MessageFormat;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * 邮件发送定时任务
 * 用于定期发送系统邮件通知
 */
@Slf4j
public class EmailJob implements Job {

  /**
   * 执行邮件发送任务
   * @param context 任务执行上下文,包含任务相关参数
   */
  @Override
  public void execute(JobExecutionContext context) {
    // 从上下文中获取用户邮箱
    String userEmail = context.getJobDetail().getJobDataMap().getString("userEmail");
    
    // 记录任务执行信息
    log.info(
        MessageFormat.format(
            "开始执行邮件任务: 任务名称={0}, 收件人={1}, 执行线程={2}",
            getClass().getSimpleName(),
            userEmail,
            Thread.currentThread().getName())
    );
  }
}
