package de.ace.html2pdf.application;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import de.ace.html2pdf.config.DavidPDFException;
import de.ace.html2pdf.model.FooterStyle;
import de.ace.html2pdf.model.footer.Footer;
import de.ace.html2pdf.model.footer.FooterMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostProcessorPdfComponent {

    public byte[] attachFooter(Optional<Element> footerElement, FooterStyle footerStyle, ByteArrayOutputStream pdfRenderStream) {
        try (
                PdfReader reader = new PdfReader(pdfRenderStream.toByteArray());
                ByteArrayOutputStream pdfWithFooterStream = new ByteArrayOutputStream();
                PdfStamper stamper = new PdfStamper(reader, pdfWithFooterStream);
        ) {

            footerElement.ifPresent(element -> {
                for (int page = 1; page <= reader.getNumberOfPages(); page++) {
                    PdfContentByte over = stamper.getOverContent(page);

                    Footer footer = FooterMapper.footerFrom(element,
                            FooterMapper.themeFrom(footerStyle, reader, page),
                            page);
                    footer.showOn(over);
                }
            });

            stamper.close();
            reader.close();

            return pdfWithFooterStream.toByteArray();
        } catch (Exception e) {
            throw DavidPDFException.Type.CORRUPTED_STREAM.boom(e);
        }
    }

}
