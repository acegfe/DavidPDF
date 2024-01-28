package de.ace.html2pdf.mixin;

import com.lowagie.text.Element;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import lombok.Getter;

@Getter
public class Row {

    private String text;
    private Theme theme;
    private BaseFont baseFont;

    public Row(String text, Theme theme) {
        this.baseFont = calculateBaseFont(text);
        this.text = text.replaceAll("\\[b\\]", "");
        this.theme = theme;
    }

    public void operate(PdfContentByte over, float startingX, float startingY) {
        over.beginText();
        over.setFontAndSize(baseFont, theme.fontSize());
        over.setTextMatrix(startingX, startingY);
        over.showTextAligned(Element.ALIGN_LEFT, getText(), startingX, startingY, 0);
        over.endText();
    }

    private BaseFont calculateBaseFont(String text) {
        if (text.contains(Decorator.BOLD.getEffect())) {
            return FooterMapper.getBaseFont(true);
        }

        return FooterMapper.getBaseFont(false);
    }
}
