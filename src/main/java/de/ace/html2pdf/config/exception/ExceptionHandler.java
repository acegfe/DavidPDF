package de.ace.html2pdf.config.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpStatus.I_AM_A_TEAPOT;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(PdfException.class)
    public ResponseEntity<byte[]> exceptionHandler(PdfException exception) {
        return ResponseEntity.status(I_AM_A_TEAPOT).body(exception.getMessage().getBytes(StandardCharsets.UTF_8));
    }
}
