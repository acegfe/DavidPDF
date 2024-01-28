package de.ace.html2pdf.config;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DavidPDFException extends RuntimeException {
    public DavidPDFException(String message) {
        super(message);
    }

    public enum Type {
        CORRUPTED_STREAM("We could not process your stream"),
        UNABLE_TO_WEBDRIVER("There was an issue during the Web Driver creation"),
        GENERIC_MARK("David you are mess");

        private final String message;

        Type(String message) {
            this.message = message;
        }

        public DavidPDFException boom(Exception e) {
            log.error(e.getMessage());
            return new DavidPDFException(message);
        }

    }

}
