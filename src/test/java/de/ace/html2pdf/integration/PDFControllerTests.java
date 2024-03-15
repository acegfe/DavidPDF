package de.ace.html2pdf.integration;

import de.ace.html2pdf.config.ApplicationValuesConfig;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class PDFControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ApplicationContext applicationContext;

  static ApplicationValuesConfig config;

 private static final DockerImageName IMAGENAME = DockerImageName.parse("seleniarm/standalone-chromium:latest").asCompatibleSubstituteFor("selenium/standalone-chrome");

 @Container
  private static final BrowserWebDriverContainer<?> chromeContainer = new BrowserWebDriverContainer<>(IMAGENAME)
        .withCapabilities(new ChromeOptions().addArguments("--headless", "--no-sandbox"));

  @DynamicPropertySource
  static void registerDynamicProperties(DynamicPropertyRegistry registry) {
    registry.add(config.getPath(), () -> "http://" + chromeContainer.getHost() + ":" + chromeContainer.getMappedPort(4444));
  }

  @Test
  public void testMockMvcBeanExists() {
    Assertions.assertNotNull(applicationContext.getBean(MockMvc.class));
  }

    public void givenHTML_whenAPIIsCalled_thenReturnPDF() throws Exception {
      String html="<!DOCTYPE html>\n"
          + "<html lang=\"en\">\n"
          + "<head>\n"
          + "    <meta charset=\"UTF-8\">\n"
          + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
          + "    <title>Test HTML Page</title>\n"
          + "</head>\n"
          + "<body>\n"
          + "    <header>\n"
          + "        <h1>Test HTML Page</h1>\n"
          + "    </header>\n"
          + "    <main>\n"
          + "        <p>This is a sample HTML page for testing.</p>\n"
          + "    </main>\n"
          + "    <footer>\n"
          + "        <p>Footer content goes here.</p>\n"
          + "    </footer>\n"
          + "</body>\n"
          + "</html>\n";

      mockMvc.perform(MockMvcRequestBuilders.post("/pdf/html")
              .content(html))
          .andExpect(MockMvcResultMatchers.status().isOk());

    }

}
