package de.ace.html2pdf.application;

import de.ace.html2pdf.config.ApplicationValuesConfig;
import de.ace.html2pdf.config.DavidPDFException;
import de.ace.html2pdf.model.FooterProperties;
import de.ace.html2pdf.model.PdfData;
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

    public PdfData parseHtmlToPdf(final String html) {
        var driver = createRemoteDriver(config.getPath());
        String footerHtml = clearBesidesFooter(html);
        FooterProperties footerProperties = calculateFooterProperties(html, driver);
        String mainHtml = addMarginStyle(clearFooter(html), footerProperties.height());
        var pdfData = PdfData.builder()
                .mainBytes(renderPdf(mainHtml, driver))
                .footerBytes(renderPdf(footerHtml, driver))
                .footerProperties(footerProperties)
                .build();
        driver.quit();
        return pdfData;
    }

    private String addMarginStyle(String html, int footerHeight) {
        Document doc = Jsoup.parse(html);
        Element styleTag = doc.selectFirst("style");
        if (styleTag != null) {
            String existingStyle = styleTag.html();
            String newStyle = String.format("\n@page { margin-bottom: %dpx }", footerHeight);
            String combinedStyle = existingStyle + newStyle;
            styleTag.text(combinedStyle);
        }
        return doc.outerHtml();
    }

    private String clearFooter(final String html) {
        var document = Jsoup.parse(html);
        document.getElementsByTag("footer").clear();
        return document.outerHtml();
    }

    private String clearBesidesFooter(final String html) {
        var document = Jsoup.parse(html);
        Elements footerSiblings = document.selectFirst("footer").siblingElements();
        footerSiblings.clear();
        return document.outerHtml();
    }

    private FooterProperties calculateFooterProperties(String html, final WebDriver driver) {
        driver.get("data:text/html," + UriEncoder.encode(html));
        WebElement element = driver.findElement(By.tagName("footer"));
        int width = element.getSize().getWidth();
        int height = element.getSize().getHeight();
        int x = element.getLocation().x;
        int y = 842 - height;
        return new FooterProperties(x, y, width, height);
    }

    private byte[] renderPdf(final String data, WebDriver driver) {
        driver.get("data:text/html," + UriEncoder.encode(data));

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));

        PrintOptions printOptions = new PrintOptions();
        printOptions.setBackground(true);
        var pdf = ((PrintsPage) driver).print(printOptions);

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
