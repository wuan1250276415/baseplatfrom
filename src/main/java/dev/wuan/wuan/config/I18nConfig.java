package dev.wuan.wuan.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * 国际化配置类
 * 用于配置应用程序的国际化(i18n)支持
 */
@Configuration
public class I18nConfig {

  /**
   * 配置消息源
   * 用于加载和管理国际化资源文件
   * @return ReloadableResourceBundleMessageSource实例
   */
  @Bean
  public ReloadableResourceBundleMessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource =
        new ReloadableResourceBundleMessageSource();
    // 设置国际化资源文件的基础名
    messageSource.setBasename("classpath:i18n/messages");
    // 设置默认编码
    messageSource.setDefaultEncoding("UTF-8");
    // 禁用消息格式化
    messageSource.setAlwaysUseMessageFormat(false);
    // 禁用回退到系统区域设置
    messageSource.setFallbackToSystemLocale(false);
    // 当消息代码未找到时使用代码作为默认消息
    messageSource.setUseCodeAsDefaultMessage(true);
    // 设置默认区域为美国
    messageSource.setDefaultLocale(Locale.US);
    // 设置缓存时间为1小时
    messageSource.setCacheSeconds(3600);
    return messageSource;
  }

  /**
   * 配置区域解析器
   * 用于确定当前请求的区域设置
   * @return LocaleResolver实例
   */
  @Bean
  public LocaleResolver localeResolver() {
    // 创建自定义的AcceptHeaderLocaleResolver
    AcceptHeaderLocaleResolver localeResolver =
        new AcceptHeaderLocaleResolver() {
          @Override
          public Locale resolveLocale(HttpServletRequest request) {
            // 尝试从请求参数中获取语言设置
            String locale = request.getParameter("lang");
            // 如果指定了语言参数则使用它，否则使用Accept-Language头
            return locale != null
                ? org.springframework.util.StringUtils.parseLocaleString(locale)
                : super.resolveLocale(request);
          }
        };
    // 设置默认区域为美国
    localeResolver.setDefaultLocale(Locale.US);
    return localeResolver;
  }
}
