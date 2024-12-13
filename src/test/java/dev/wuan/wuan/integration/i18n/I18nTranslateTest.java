package dev.wuan.wuan.integration.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.wuan.wuan.config.I18nConfig;
import dev.wuan.wuan.config.security.HttpFireWallConfig;
import dev.wuan.wuan.controller.SignController;
import jakarta.annotation.Resource;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.LocaleResolver;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@WebMvcTest(value = {SignController.class})
@Import({HttpFireWallConfig.class})
@ContextConfiguration(classes = I18nConfig.class)
public class I18nTranslateTest {

  @Resource private MessageSource messageSource;

  @Resource private LocaleResolver localeResolver;

  @Test
  void getI18nMessage_givenLocaleToMessageSource_shouldReturnCorrespondingMessage() {
    String messageCn = messageSource.getMessage("test", new Object[] {"Java"}, Locale.CHINA);
    assertEquals("让 Java 再次伟大", messageCn);

    String messageEn = messageSource.getMessage("test", new Object[] {"Java"}, Locale.US);
    assertEquals("Make Java Great Again", messageEn);
  }

  @Test
  void localeResolve_givenLocaleToLocaleResolver_shouldReturnCorrectLocale() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Accept-Language", "zh");
    Locale locale = localeResolver.resolveLocale(request);
    assertEquals(request.getLocale(), locale);
    assertEquals("zh", locale.getLanguage());

    MockHttpServletRequest request2 = new MockHttpServletRequest();
    request2.addHeader("Accept-Language", "zh");
    request2.addParameter("lang", "en");
    Locale locale2 = localeResolver.resolveLocale(request2);
    assertEquals("en", locale2.getLanguage());

    MockHttpServletRequest request3 = new MockHttpServletRequest();
    Locale locale3 = localeResolver.resolveLocale(request3);
    assertEquals("en", request3.getLocale().getLanguage());
    assertEquals("en", locale3.getLanguage());

    MockHttpServletRequest request4 = new MockHttpServletRequest();
    request4.addHeader("Accept-Language", "fr");
    Locale locale4 = localeResolver.resolveLocale(request4);
    /* Simulate the process of RequestContextFilter setting the locale from
    request.locale */
    LocaleContextHolder.setLocale(request4.getLocale(), false);
    Locale contextLocale = LocaleContextHolder.getLocale();
    assertEquals("fr", locale4.getLanguage());
    assertEquals(contextLocale, locale4);
  }
}
