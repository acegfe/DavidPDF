package de.ace.html2pdf.application;

import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfReader;
import de.ace.html2pdf.config.DavidPDFException;
import de.ace.html2pdf.model.PdfRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
        byte[] mainBytes = pdfRenderComponent.clearFooter(html);
        byte[] footerBytes = pdfRenderComponent.clearBesidesFooter(html);
        try (PDDocument mainDocument = PDDocument.load(new ByteArrayInputStream(mainBytes));
             PDDocument footerDocument = PDDocument.load(new ByteArrayInputStream(footerBytes));
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            var pdImage = getPrintingImage(footerDocument, mainDocument, "footer");
            for (var page : mainDocument.getPages()) {
                PDPageContentStream contentStream = new PDPageContentStream(mainDocument, page, PDPageContentStream.AppendMode.APPEND, true);
                contentStream.drawImage(pdImage, 0, 0);
                contentStream.close();
            }
            mainDocument.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DavidPDFException("Shit");
        }
    }

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
