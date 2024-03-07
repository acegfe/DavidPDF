package de.ace.html2pdf.controller;

import de.ace.html2pdf.application.PdfRenderComponent;
import de.ace.html2pdf.application.PdfService;
import de.ace.html2pdf.config.DavidPDFException;
import de.ace.html2pdf.model.PdfRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpStatus.OK;

/**
 * To be used with Docker. This requires that you build, compile and use docker-compose
 */
@Slf4j
@RestController
@RequestMapping("/pdf")
@RequiredArgsConstructor
public class PdfGenerationController {

    private final PdfService pdfService;
    private final PdfRenderComponent pdfRenderComponent;

    @GetMapping("/healthCheck")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("\uD83D\uDD25");
    }

    @PostMapping("/html")
    public ResponseEntity<byte[]> generatePdfHtml(@RequestBody PdfRequest pdfRequest) {
        try {
            return new ResponseEntity<>(pdfService.convert(pdfRequest),
                    pdfContentTypeHeader(), OK);
        } catch (DavidPDFException e) {
            return ResponseEntity.status(418).body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }

    @PostMapping("/footer")
    public ResponseEntity<byte[]> extractFooter(@RequestBody String html) {
        return new ResponseEntity<>(pdfRenderComponent.parseHtmlToPdf(html).getFooterBytes(), pdfContentTypeHeader(), OK);
    }

    @PostMapping("/footer/text")
    public ResponseEntity<String> extractFooterText(@RequestBody String html) {
        return new ResponseEntity<>(pdfService.extractFooterText(html), OK);
    }

    @PostMapping("/test")
    public ResponseEntity<byte[]> footerAsImage(@RequestBody String html) {
        return new ResponseEntity<>(pdfService.html2pdf(html), pdfContentTypeHeader(), OK);
    }

    @PostMapping("/main")
    public ResponseEntity<byte[]> merge(@RequestBody String html) {
        return new ResponseEntity<>(pdfRenderComponent.parseHtmlToPdf(html).getMainBytes(), pdfContentTypeHeader(), OK);
    }

    private HttpHeaders pdfContentTypeHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        return httpHeaders;
    }

    private HttpHeaders jpegContentTypeHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_PNG);
        return httpHeaders;
    }
}
