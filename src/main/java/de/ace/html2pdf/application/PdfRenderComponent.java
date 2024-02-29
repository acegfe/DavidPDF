package de.ace.html2pdf.application;

import de.ace.html2pdf.config.ApplicationValuesConfig;
import de.ace.html2pdf.config.DavidPDFException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.util.UriEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import static java.util.Base64.getDecoder;

@Component
@RequiredArgsConstructor
@Slf4j
public class PdfRenderComponent {

    private final ApplicationValuesConfig config;

    /**
     * @param html                  HTML data of website
     * @param byteArrayOutputStream Output of rendering
     * @return Footer if available
     */
    public Optional<Element> render(String html, ByteArrayOutputStream byteArrayOutputStream) throws IOException {
        Document document = Jsoup.parse(html);
        Optional<Element> footer = extractFooterTag(document);

        byteArrayOutputStream.write(renderPdf(document.toString(), createRemoteDriver(config.getPath())));
        return footer;
    }

    private Optional<Element> extractFooterTag(Document document) {
        var footer = document.getElementsByTag("footer").stream().findFirst();
        document.getElementsByTag("footer").clear();
        return footer;
    }

    public byte[] clearFooter(final String html) {
        var document = Jsoup.parse(html);
        document.getElementsByTag("footer").clear();
        var driver = createRemoteDriver(config.getPath());
        return renderPdf(document.outerHtml(), driver);
    }

    public byte[] clearBesidesFooter(final String html) {
        var document = Jsoup.parse(html);
        var driver = createRemoteDriver(config.getPath());
        Elements footerSiblings = document.selectFirst("footer").siblingElements();
        footerSiblings.clear();
        var footerHtml = document.outerHtml();
        log.info("Footer height: {}", calculateElementHeight(footerHtml, "footer", driver));
        return renderPdf(footerHtml, driver);
    }

    public int calculateElementHeight(String html, String elementTag, final WebDriver driver) {
        driver.get("data:text/html," + UriEncoder.encode(html));
        WebElement element = driver.findElement(By.tagName(elementTag));
        return element.getSize().getHeight();
    }

    private byte[] renderPdf(final String data, final WebDriver driver) {
        driver.get("data:text/html," + UriEncoder.encode(data));

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));

        PrintOptions printOptions = new PrintOptions();
        printOptions.setBackground(true);
        var pdf = ((PrintsPage) driver).print(printOptions);

        if (driver instanceof RemoteWebDriver remoteWebDriver) {
            remoteWebDriver.quit();
        }

        return getDecoder().decode(pdf.getContent());
    }

    private RemoteWebDriver createRemoteDriver(String url) {
        ChromeOptions chromeOptions = new ChromeOptions().addArguments("--headless", "--no-sandbox");
        try {
            return new RemoteWebDriver(new URI(url).toURL(), chromeOptions);
        } catch (Exception e) {
            throw DavidPDFException.Type.UNABLE_TO_WEBDRIVER.boom(e);
        }
    }
}
