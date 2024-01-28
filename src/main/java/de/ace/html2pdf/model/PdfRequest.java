package de.ace.html2pdf.model;

public record PdfRequest(
        String html,
        FooterStyle footerStyle
) {
}
