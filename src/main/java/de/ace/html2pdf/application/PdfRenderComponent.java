package de.ace.html2pdf.application;

import de.ace.html2pdf.config.ApplicationValuesConfig;
import de.ace.html2pdf.config.exception.PdfException;
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

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import static de.ace.html2pdf.config.Constants.IMAGE_HEIGHT;
import static de.ace.html2pdf.config.Constants.PDF_DEFAULT_PADDING;
import static java.util.Base64.getDecoder;
import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
@Slf4j
public class PdfRenderComponent {

    private final ApplicationValuesConfig config;

    public PdfData parseHtmlToPdf(final String html) {
        var driver = createRemoteDriver(config.getPath());
        String footerHtml = clearBesidesFooter(html);
        FooterProperties footerProperties = calculateFooterProperties(html, driver);
        String mainHtml = addMarginStyle(clearFooter(html), footerProperties.getHtmlHeight());
        PdfData pdfData = new PdfData(renderPdf(mainHtml, driver), renderPdf(footerHtml, driver), footerProperties);
        driver.quit();
        return pdfData;
    }

    private String addMarginStyle(String html, int footerHeight) {
        Document doc = Jsoup.parse(html);
        Element styleTag = doc.selectFirst("style");
        if (styleTag != null) {
            String existingStyle = styleTag.html();
            String newStyle = String.format("\n@page { margin-bottom: %dpx }", footerHeight + PDF_DEFAULT_PADDING); //1 inch = 96 css px, pdf default margin is 0.5 inch
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
        Optional<Elements> footerSiblings = ofNullable(document.selectFirst("footer")).map(Element::siblingElements);
        footerSiblings.ifPresent(Elements::clear);
        return document.outerHtml();
    }

    private FooterProperties calculateFooterProperties(String html, final WebDriver driver) {
        driver.get("data:text/html," + UriEncoder.encode(html));
        WebElement element = driver.findElement(By.tagName("footer"));
        int htmlWidth = element.getSize().getWidth();
        int htmlHeight = element.getSize().getHeight();
        int width = (int) Math.round((double) htmlWidth * 3.2); // 3.2 = X dimensions difference coefficient
        int height = (int) Math.round((double) htmlHeight * 3.65); // 3.65 = Y dimensions difference coefficient
        int x = element.getLocation().x;
        int y = IMAGE_HEIGHT - height - PDF_DEFAULT_PADDING;
        width -= x;
        return new FooterProperties(x, y, width, height, htmlWidth, htmlHeight);
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
            throw PdfException.Type.UNABLE_TO_WEBDRIVER.pdfException(e);
        }
    }
}
