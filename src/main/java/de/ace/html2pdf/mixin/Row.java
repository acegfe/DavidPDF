package de.ace.html2pdf.mixin;

import com.lowagie.text.Element;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Row {

    private String text;
    private Theme theme;

    public void operate(PdfContentByte over, float startingX, float startingY) {
        over.beginText();
        over.setFontAndSize(calculateBaseFontAndUpdate(text), theme.fontSize());
        over.setTextMatrix(startingX, startingY);
        over.showTextAligned(Element.ALIGN_LEFT, getText(), startingX, startingY, 0);
        over.endText();
    }

    public String getText() {
        if (text.contains(Decorator.BOLD.getEffect())) {
            this.text = text.replaceAll("/[b/]", "");
        }

        return text;
    }

    private BaseFont calculateBaseFontAndUpdate(String text) {
        if (text.contains(Decorator.BOLD.getEffect())) {
            return FooterMapper.getBaseFont(true);
        }

        return FooterMapper.getBaseFont(false);
    }
}
