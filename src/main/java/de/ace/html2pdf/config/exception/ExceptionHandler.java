package de.ace.html2pdf.config.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.I_AM_A_TEAPOT;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(PdfException.class)
    public ResponseEntity<byte[]> pdfExceptionHandler(PdfException exception) {
        return ResponseEntity.status(I_AM_A_TEAPOT).body(exception.getMessage().getBytes(StandardCharsets.UTF_8));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IOException.class)
    public ResponseEntity<byte[]> IOExceptionHandler(IOException exception) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(exception.getMessage().getBytes(StandardCharsets.UTF_8));
    }
}
