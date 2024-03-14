import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.utility.DockerImageName;

public class PdfServiceTest {
  final DockerImageName imageName = DockerImageName.parse("seleniarm/standalone-chromium:latest");

    @Rule
    public BrowserWebDriverContainer<?> chromeContainer = new BrowserWebDriverContainer<>()
        .withCapabilities(new ChromeOptions());


    @Test
    public void checkIfDriverCreated(){
      RemoteWebDriver driver = new RemoteWebDriver(chromeContainer.getSeleniumAddress(),
          new ChromeOptions().addArguments("--headless", "--no-sandbox"));
      driver.get("https://www.google.com/");
      Assert.assertEquals("Google",driver.getTitle());
    }



}
