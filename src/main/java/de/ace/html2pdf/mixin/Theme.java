package de.ace.html2pdf.mixin;

import com.lowagie.text.pdf.BaseFont;

public record Theme(
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
