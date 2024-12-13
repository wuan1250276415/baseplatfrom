package dev.wuan.wuan.integration.mvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.wuan.wuan.config.StaticResourceHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = {StaticResourceHandler.class})
@Import(StaticResourceHandler.class)
public class StaticResourceHandlerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  @WithMockUser
  public void testStaticResourceHandler() throws Exception {
    mockMvc
        .perform(get("/asset/test.tar.gz").header("Accept-Encoding", "gzip, deflate, br, zstd"))
        .andExpect(status().isOk())
        .andExpect(header().string("Cache-Control", "max-age=60"))
        .andExpect(content().contentType("application/octet-stream"));
  }
}
