package de.ace.html2pdf.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PdfData {

    private byte[] mainBytes;
    private byte[] footerBytes;
    private FooterProperties footerProperties;
}
