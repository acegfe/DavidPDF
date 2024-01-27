package de.ace.html2pdf.application;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.Optional;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostProcessorPdfComponent {

    public byte[] attachFooter(String footer, ByteArrayOutputStream pdfRenderStream) {

        System.out.println(footer);

        try (
                PdfReader reader = new PdfReader(pdfRenderStream.toByteArray());
                ByteArrayOutputStream pdfWithFooterStream = new ByteArrayOutputStream();
                PdfStamper stamper = new PdfStamper(reader, pdfWithFooterStream);
        ) {
            System.out.println(reader.getNumberOfPages());
            System.out.println(reader.getPageSize(1));

            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            var document = Jsoup.parse(footer).getElementsByTag("footer").get(0);

            // Loop over each page and add a footer
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                float fontSize = 6;

                float bottomMargin = 25;
                float verticalSpacing = 6;

                float leftAbsoluteMargin = 50;
                float rightAbsoluteMargin = 30;

                float pageWidth = reader.getPageSize(i).getWidth();

                float maxXPosition = pageWidth - rightAbsoluteMargin;
                float xMarginOffset = 0;
                float portionsOfX = ((maxXPosition - leftAbsoluteMargin) / document.children().size()) + xMarginOffset;

                PdfContentByte over = stamper.getOverContent(i);

                for (int j = 0; j < document.children().size(); ++j) {
                    var column = document.children().get(j);

                    float startingX = leftAbsoluteMargin + (portionsOfX*j);
                    float startingY = (verticalSpacing*(column.children().size()-1))+bottomMargin;

                    over.beginText();
                    over.setFontAndSize(baseFont, fontSize);
                    over.setTextMatrix(startingX, startingY);

                    for (int x = 0; x < column.children().size(); ++x) {
                        var row = column.children().get(x);

                        var text = Optional.ofNullable(row.text()).orElse("").replace("[PAGE_COUNTER]", String.format("Page %s/%s", i, reader.getNumberOfPages()));
                        over.showTextAligned(Element.ALIGN_LEFT, text, startingX, startingY - (x*verticalSpacing), 0);
                    }

                    over.endText();

                }
            }

            stamper.close();
            reader.close();

            return pdfWithFooterStream.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

}
