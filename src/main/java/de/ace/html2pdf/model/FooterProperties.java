package de.ace.html2pdf.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FooterProperties {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int htmlWidth;
    private final int htmlHeight;
    private FooterImageProperties footerImageProperties;
}
