package dev.misei.html2pdf.controller;

import dev.misei.html2pdf.application.PdfRenderComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static dev.misei.html2pdf.model.Constants.SELENIARM;
import static dev.misei.html2pdf.model.RenderType.TYPE_DATA;
import static dev.misei.html2pdf.model.RenderType.TYPE_URL;

/**
 * To be used with Docker. This requires that you build, compile and use docker-compose
 */
@Controller
@RequestMapping("/docker")
@Slf4j
@RequiredArgsConstructor
public class ApiController {

    public final PdfRenderComponent renderComponent;

    @GetMapping("/healthCheck")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Ok");
    }

    @GetMapping("/generatePdfUrl")
    public ResponseEntity<byte[]> generatePdfUrl(@RequestParam String url) throws MalformedURLException, URISyntaxException {
        return new ResponseEntity<>(renderComponent.render(url, PdfRenderComponent.createRemoteDriver(SELENIARM), TYPE_URL), pdfContentTypeHeader(), HttpStatus.OK);
    }

    @PostMapping("/generatePdfHtml")
    public ResponseEntity<byte[]> generatePdfHtml(@RequestBody String htmlData) throws MalformedURLException, URISyntaxException {
        return new ResponseEntity<>(renderComponent.render(htmlData, PdfRenderComponent.createRemoteDriver(SELENIARM), TYPE_DATA), pdfContentTypeHeader(), HttpStatus.OK);
    }

    private HttpHeaders pdfContentTypeHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        return httpHeaders;
    }
}
