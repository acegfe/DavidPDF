package de.ace.html2pdf.controller;

import de.ace.html2pdf.application.PdfService;
import de.ace.html2pdf.config.DavidPDFException;
import de.ace.html2pdf.model.PdfRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

/**
 * To be used with Docker. This requires that you build, compile and use docker-compose
 */
@Slf4j
@RestController
@RequestMapping("/pdf")
@RequiredArgsConstructor
public class PdfGenerationController {

    public final PdfService pdfService;

    @GetMapping("/healthCheck")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("\uD83D\uDD25");
    }

    @PostMapping("/html")
    public ResponseEntity<byte[]> generatePdfHtml(@RequestBody PdfRequest pdfRequest) {
        try {
            return new ResponseEntity<>(pdfService.convert(pdfRequest),
                    pdfContentTypeHeader(), HttpStatus.OK);
        } catch (DavidPDFException e) {
            return ResponseEntity.status(418).body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }

    private HttpHeaders pdfContentTypeHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        return httpHeaders;
    }
}
