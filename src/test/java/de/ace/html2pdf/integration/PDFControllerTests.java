package de.ace.html2pdf.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PDFControllerTests {

  @Autowired
  private MockMvc mockMvc;


  @Value("classpath:input.html")
  private Resource inputFile;

 private static final DockerImageName IMAGENAME = DockerImageName.parse("seleniarm/standalone-chromium:latest")
     .asCompatibleSubstituteFor("selenium/standalone-chrome");

 @Container
  private static final BrowserWebDriverContainer<?> chromeContainer = new BrowserWebDriverContainer<>(IMAGENAME)
        .withCapabilities(new ChromeOptions().addArguments("--headless", "--no-sandbox"));

  @BeforeAll
  static void setUp() {
    chromeContainer.start();
  }

  @DynamicPropertySource
  static void registerDynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("dynamic.selenium.driver-path", () -> "http://" + chromeContainer.getHost() + ":" + chromeContainer.getMappedPort(4444));
  }


  @Test
    public void givenHTML_whenAPIIsCalled_thenReturnPDF() throws Exception {

      String input = inputFile.getContentAsString(StandardCharsets.UTF_8);

      mockMvc.perform(post("/pdf/html")
              .content(input)
              .header("Authorization","Bearer prodkey"))
          .andExpect(status().isOk())
          .andExpect(header().string("Content-Type","application/pdf"));

    }

}
