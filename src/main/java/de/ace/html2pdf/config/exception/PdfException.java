package de.ace.html2pdf.config.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
public class PdfException extends RuntimeException {
    public PdfException(String message) {
        super(message);
    }

    public enum Type {
        CORRUPTED_STREAM("We could not process your stream"),
        UNABLE_TO_WEBDRIVER("There was an issue during the Web Driver creation");

        private final String message;

        Type(String message) {
            this.message = message;
        }

        public PdfException boom(Exception e) {
            log.error(e.getMessage());
            return new PdfException(message);
        }

    }

}
