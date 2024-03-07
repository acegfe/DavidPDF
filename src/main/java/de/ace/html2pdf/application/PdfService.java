package de.ace.html2pdf.application;


import de.ace.html2pdf.config.DavidPDFException;
import de.ace.html2pdf.model.FooterProperties;
import de.ace.html2pdf.model.PdfData;
import de.ace.html2pdf.model.PdfRequest;
import io.github.jonathanlink.PDFLayoutTextStripper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
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

    @SneakyThrows
    public String extractFooterText(String html) {
        PdfData pdfData = pdfRenderComponent.parseHtmlToPdf(html);
        var bais = new ByteArrayInputStream(pdfData.getFooterBytes());
        try {
            PDFParser pdfParser = new PDFParser(new RandomAccessBufferedFileInputStream(bais));
            pdfParser.parse();
            PDDocument pdDocument = new PDDocument(pdfParser.getDocument());
            PDFTextStripper pdfTextStripper = new PDFLayoutTextStripper();
            String result = pdfTextStripper.getText(pdDocument);
            System.out.println(result);
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }

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
            var image = pdfRenderer.renderImage(0, 1);
            log.info("image: {}", image.getWidth());
            var clipped = image.getSubimage(fp.x(), fp.y(), fp.width(), fp.height());
            return toByteArray(flipImageVertically(clipped), "png");
        }
    }

    @SneakyThrows
    private void drawFooterOnPage(PDDocument document, int pageNumber, byte[] imageBytes, FooterProperties fp) {
        PDPage page = document.getPage(pageNumber);
        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageBytes, "footerImage");
        try (PDPageContentStream contents = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, false)) {
            contents.drawImage(pdImage, fp.x(), fp.y());
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
