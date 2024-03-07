package de.ace.html2pdf.application;


import de.ace.html2pdf.model.FooterImageProperties;
import de.ace.html2pdf.model.FooterProperties;
import de.ace.html2pdf.model.PdfData;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static de.ace.html2pdf.model.Constants.IMAGE_HEIGHT;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfService {

    private final PdfRenderComponent pdfRenderComponent;

    @SneakyThrows
    public byte[] html2pdf(String html) {
        PdfData pdfData = pdfRenderComponent.parseHtmlToPdf(html);
        byte[] clippedFootedBytes = clipFooter(pdfData.getFooterBytes(), pdfData.getFooterProperties());
        try (PDDocument mainDoc = PDDocument.load(pdfData.getMainBytes());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (int pageNumber = 0; pageNumber < mainDoc.getPages().getCount(); ++pageNumber) {
                drawFooterOnPage(mainDoc, pageNumber, clippedFootedBytes, pdfData.getFooterProperties());
            }
            mainDoc.save(baos);
            return baos.toByteArray();
        }
    }

    @SneakyThrows
    private byte[] clipFooter(byte[] footerBytes, FooterProperties fp) {
        try (PDDocument footerDoc = PDDocument.load(footerBytes)) {
            var pdfRenderer = new PDFRenderer(footerDoc);
            pdfRenderer.setImageDownscalingOptimizationThreshold(0);
            var image = pdfRenderer.renderImageWithDPI(0, 300);
            var clipped = image.getSubimage(fp.getX(), fp.getY(), fp.getWidth(), fp.getHeight());
            FooterImageProperties footerImageProperties = new FooterImageProperties(clipped.getWidth(), clipped.getHeight());
            fp.setFooterImageProperties(footerImageProperties);
            return toPngByteArray(flipImageVertically(clipped));
        }
    }

    @SneakyThrows
    private void drawFooterOnPage(PDDocument document, int pageNumber, byte[] imageBytes, FooterProperties fp) {
        PDPage page = document.getPage(pageNumber);
        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageBytes, "footerImage");
        try (PDPageContentStream contents = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, false)) {
            int y = IMAGE_HEIGHT - fp.getFooterImageProperties().height();
            contents.drawImage(pdImage, 0, y);
        }
    }

    private static byte[] toPngByteArray(BufferedImage bi)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);
        return baos.toByteArray();
    }

    public static BufferedImage flipImageVertically(final BufferedImage image) {
        int x = 0;
        int w = image.getWidth();
        int h = image.getHeight();

        final BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = out.createGraphics();

        int y = h;
        h *= -1;

        g2d.drawImage(image, x, y, w, h, null);
        g2d.dispose();

        return out;
    }
}
