import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.ace.html2pdf.MagicApplication;
import de.ace.html2pdf.application.PdfRenderComponent;
import de.ace.html2pdf.application.PdfService;
import de.ace.html2pdf.config.ApplicationValuesConfig;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = MagicApplication.class)
@AutoConfigureMockMvc
@Testcontainers
public class PDFControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  ApplicationValuesConfig config;

  @Autowired
  PdfService pdfService;

  @Autowired
  PdfRenderComponent pdfRenderComponent;
  final DockerImageName IMAGENAME = DockerImageName.parse("seleniarm/standalone-chromium:latest").asCompatibleSubstituteFor("selenium/standalone-chrome");

    @Rule
    public BrowserWebDriverContainer<?> chromeContainer = new BrowserWebDriverContainer<>(IMAGENAME)
        .withCapabilities(new ChromeOptions());


    @Test
    public void checkIfDriverCreated() throws Exception {
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
      RemoteWebDriver driver = new RemoteWebDriver(chromeContainer.getSeleniumAddress(),
          new ChromeOptions().addArguments("--headless", "--no-sandbox"));
      when(any(PdfRenderComponent.class).createRemoteDriver(config.getPath())).thenReturn(driver);
      mockMvc.perform(MockMvcRequestBuilders.post("/pdf/html")
              .content(html))
          .andExpect(MockMvcResultMatchers.status().isOk());

    }


}
