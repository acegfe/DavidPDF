package de.ace.html2pdf.application.footer;

import com.lowagie.text.pdf.BaseFont;

public record Style(
        BaseFont baseFont,
        float fontSize,
        float verticalSpacing,
        float bottomMargin,
        float leftAbsoluteMargin,
        float rightAbsoluteMargin,
        float pageWidth,
        int numberPages,
        float xElementsOffset
) {
}
