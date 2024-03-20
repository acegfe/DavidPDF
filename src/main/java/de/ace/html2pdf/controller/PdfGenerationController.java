package de.ace.html2pdf.controller;

import de.ace.html2pdf.application.PdfRenderComponent;
import de.ace.html2pdf.application.PdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    public ResponseEntity<byte[]> generatePdfHtml(@RequestBody String html) throws IOException {
        return new ResponseEntity<>(pdfService.html2pdf(html), pdfContentTypeHeader(), OK);
    }

    @PostMapping("/main") //Debug
    public ResponseEntity<byte[]> merge(@RequestBody String html) {
        return new ResponseEntity<>(pdfRenderComponent.parseHtmlToPdf(html).mainBytes(), pdfContentTypeHeader(), OK);
    }

    @PostMapping("/footer") //Debug
    public ResponseEntity<byte[]> extractFooter(@RequestBody String html) {
        return new ResponseEntity<>(pdfRenderComponent.parseHtmlToPdf(html).footerBytes(), pdfContentTypeHeader(), OK);
    }

    private static HttpHeaders pdfContentTypeHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        return httpHeaders;
    }

}
