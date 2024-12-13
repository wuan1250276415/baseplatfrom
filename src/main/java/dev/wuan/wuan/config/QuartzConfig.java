package dev.wuan.wuan.config;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import dev.wuan.wuan.job.DataBackupJob;

/**
 * Quartz定时任务配置类
 * 用于配置定时任务的调度器、触发器和任务详情
 */
@Configuration
public class QuartzConfig {

  /**
   * 配置邮件任务调度器工厂
   * @param dataSource 数据源
   * @return 调度器工厂Bean
   */
  @Bean("emailJobSchedulerFactory")
  public SchedulerFactoryBean emailJobSchedulerFactory(DataSource dataSource) {
    SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
    schedulerFactory.setSchedulerName("email-scheduler");
    Properties props = getCommonProps();
    // 设置邮件任务线程池大小为10
    props.setProperty("org.quartz.threadPool.threadCount", "10");
    schedulerFactory.setDataSource(dataSource);
    schedulerFactory.setQuartzProperties(props);
    return schedulerFactory;
  }

  /**
   * 获取Quartz通用配置属性
   * @return Quartz配置属性
   */
  private Properties getCommonProps() {
    Properties props = new Properties();
    // 使用本地数据源存储任务
    props.setProperty(
        "org.quartz.jobStore.class",
        "org.springframework.scheduling.quartz.LocalDataSourceJobStore");
    // 配置JDBC委托类
    props.setProperty(
        "org.quartz.jobStore.driverDelegateClass", 
        "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
    // 设置数据库表前缀
    props.setProperty("org.quartz.jobStore.tablePrefix", "wuan.qrtz_");
    return props;
  }

  /**
   * 配置数据备份任务详情
   * @return 任务详情工厂Bean
   */
  @Bean("dataBackupJobDetail")
  public JobDetailFactoryBean dataBackupJobDetail() {
    JobDetailFactoryBean factory = new JobDetailFactoryBean();
    factory.setJobClass(DataBackupJob.class);
    // 设置任务数据
    factory.setJobDataMap(new JobDataMap(Map.of("userId", "Gh2mxa")));
    factory.setName("data-backup-job");
    factory.setGroup("batch-service");
    factory.setDurability(true);
    return factory;
  }

  /**
   * 配置数据备份调度器工厂
   * @param dataBackupTrigger 触发器
   * @param dataBackupJobDetail 任务详情
   * @param dataSource 数据源
   * @return 调度器工厂Bean
   */
  @Bean("dataBackupSchedulerFactory")
  public SchedulerFactoryBean dataBackupSchedulerFactory(
      Trigger dataBackupTrigger, 
      JobDetail dataBackupJobDetail, 
      DataSource dataSource) {
    SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
    schedulerFactory.setSchedulerName("data-backup-scheduler");
    Properties props = getCommonProps();
    // 设置数据备份任务线程池大小为5
    props.setProperty("org.quartz.threadPool.threadCount", "5");
    schedulerFactory.setQuartzProperties(props);
    schedulerFactory.setJobDetails(dataBackupJobDetail);
    schedulerFactory.setTriggers(dataBackupTrigger);
    schedulerFactory.setDataSource(dataSource);
    return schedulerFactory;
  }

  /**
   * 配置数据备份触发器
   * @param dataBackupJobDetail 任务详情
   * @return 触发器工厂Bean
   */
  @Bean("dataBackupTrigger") 
  public CronTriggerFactoryBean dataBackupTrigger(JobDetail dataBackupJobDetail) {
    CronTriggerFactoryBean factory = new CronTriggerFactoryBean();
    factory.setJobDetail(dataBackupJobDetail);
    // 每5分钟执行一次
    factory.setCronExpression("0 0/5 * * * ?");
    return factory;
  }
}
