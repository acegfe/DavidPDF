package de.ace.html2pdf.application;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import de.ace.html2pdf.config.DavidPDFException;
import de.ace.html2pdf.model.PdfData;
import de.ace.html2pdf.model.PdfRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public byte[] mergePdf(String html) {
        PdfData pdfData = pdfRenderComponent.parseHtmlToPdf(html);
        try (PDDocument mainDocument = PDDocument.load(new ByteArrayInputStream(pdfData.getMainBytes()));
             PDDocument footerDocument = PDDocument.load(new ByteArrayInputStream(pdfData.getFooterBytes()));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            var fp = pdfData.getFooterProperties();
            PDRectangle cropBox = new PDRectangle(fp.getLocation().x, fp.getLocation().y, fp.getWidth(), fp.getHeight());
//            cropBox.getCOSArray()
//            PDPage footerPage = footerDocument.getPage(0);
//            footerPage.getMediaBox().setUpperRightX();
//            footerPage.getResources().c
//            PDPageContentStream contentStream = new PDPageContentStream()
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DavidPDFException("Shit");
        }
    }

//    @SneakyThrows
//    public byte[] foo(String html) {
//        byte[] footerBytes = pdfRenderComponent.clearBesidesFooter(html);
//        PDDocument pdDocument = PDDocument.load(footerBytes);
//        pdDocument.getPage(0).setCropBox(new PDRectangle());
//    }


    private PDImageXObject getPrintingImage(PDDocument sourceDocument, PDDocument targetDocument, String imageName) throws IOException {
        BufferedImage image = new PDFRenderer(sourceDocument).renderImage(0);
        return PDImageXObject.createFromByteArray(targetDocument, toByteArray(image, "png"), imageName);
    }

    private static byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        return baos.toByteArray();
    }
}
