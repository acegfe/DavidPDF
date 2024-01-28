package de.ace.html2pdf.mixin;

import com.lowagie.text.Element;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Row {

    private String text;
    private Theme theme;

    public void operate(PdfContentByte over, float startingX, float startingY) {
        over.beginText();
        over.setFontAndSize(calculateBaseFontAndUpdate(text), theme.fontSize());
        over.setTextMatrix(startingX, startingY);
        over.showTextAligned(Element.ALIGN_LEFT, text, startingX, startingY, 0);
        over.endText();
    }

    //TODO: Refactor. A function with two tasks... Shit code
    private BaseFont calculateBaseFontAndUpdate(String text) {
        if (text.contains(Decorator.BOLD.getEffect())) {
            this.text = text.replaceAll("/[b/]", "");
            return FooterMapper.getBaseFont(true);
        }

        return FooterMapper.getBaseFont(false);
    }
}
