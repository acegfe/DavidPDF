package de.ace.html2pdf.application;


import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import de.ace.html2pdf.config.DavidPDFException;
import de.ace.html2pdf.model.PdfData;
import de.ace.html2pdf.model.PdfRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfService {

    private final PdfRenderComponent pdfRenderComponent;
    private final PostProcessorPdfComponent postProcessorPdfComponent;

    public byte[] convert(PdfRequest pdfRequest) {
        try (ByteArrayOutputStream bs = new ByteArrayOutputStream()) {

            var footerElement = pdfRenderComponent.render(pdfRequest.html(), bs);
            return postProcessorPdfComponent.attachFooter(footerElement, pdfRequest.footerStyle(), bs);

        } catch (Exception e) {
            throw DavidPDFException.Type.CORRUPTED_STREAM.boom(e);
        }
    }

    @SneakyThrows
    public String mergePdf(String html) {
        PdfData pdfData = pdfRenderComponent.parseHtmlToPdf(html);
        var baos = new ByteArrayOutputStream();
        PdfReader pdfReader = new PdfReader(pdfData.getFooterBytes());
        String text = PdfTextExtractor.getTextFromPage(pdfReader, 1);
        return text;
    }


//    protected void manipulate1Pdf(String dest, String SRC) throws Exception {
//        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(dest));
//
//        for (int p = 1; p <= pdfDoc.getNumberOfPages(); p++) {
//            PdfPage page = pdfDoc.getPage(p);
//            Rectangle media = page.getCropBox();
//
//            if (media == null) {
//                media = page.getMediaBox();
//            }
//            float llx = media.getX() + 200;
//            float lly = media.getY() + 200;
//            float w = media.getWidth() - 400;
//            float h = media.getHeight() - 400;
//
//            // It's important to write explicit Locale settings, because decimal separator differs in
//            // different regions and in PDF only dot is respected
//            String command = String.format(Locale.ENGLISH,
//
//                    // re operator constructs a rectangle
//                    // W operator - sets the clipping path
//                    // n operator - starts a new path
//                    // q, Q - operators save and restore the graphics state stack
//                    "\nq %.2f %.2f %.2f %.2f re W n\nq\n", llx, lly, w, h);
//
//            // The content, placed on a content stream before, will be rendered before the other content
//            // and, therefore, could be understood as a background (bottom "layer")
//            PdfPage pdfPage = pdfDoc.getPage(p);
//            new PdfCanvas(pdfPage.newContentStreamBefore(), pdfPage.getResources(), pdfDoc)
//                    .writeLiteral(command);
//
//            // The content, placed on a content stream after, will be rendered after the other content
//            // and, therefore, could be understood as a foreground (top "layer")
//            new PdfCanvas(pdfPage.newContentStreamAfter(), pdfPage.getResources(), pdfDoc)
//                    .writeLiteral("\nQ\nQ\n");
//        }
//
//        pdfDoc.close();
//    }


    private static byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        return baos.toByteArray();
    }
}
