package de.ace.html2pdf.application;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import de.ace.html2pdf.application.footer.Footer;
import de.ace.html2pdf.application.footer.FooterMapper;
import de.ace.html2pdf.application.footer.Style;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostProcessorPdfComponent {

    public byte[] attachFooter(String footerText, ByteArrayOutputStream pdfRenderStream) {

        try (
                PdfReader reader = new PdfReader(pdfRenderStream.toByteArray());
                ByteArrayOutputStream pdfWithFooterStream = new ByteArrayOutputStream();
                PdfStamper stamper = new PdfStamper(reader, pdfWithFooterStream);
        ) {

            var element = Jsoup.parse(footerText).getElementsByTag("footer").get(0);

            for (int page = 1; page <= reader.getNumberOfPages(); page++) {
                PdfContentByte over = stamper.getOverContent(page);

                Footer footer = FooterMapper.from(element,
                        new Style(
                                BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED),
                                6,
                                6,
                                25,
                                50,
                                50,
                                reader.getPageSize(page).getWidth(),
                                reader.getNumberOfPages(),
                                0),
                        page);
                footer.showOn(over);
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
