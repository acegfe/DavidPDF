package de.ace.html2pdf.model;

public record PdfData(byte[] mainBytes, byte[] footerBytes, FooterProperties footerProperties) {

}
