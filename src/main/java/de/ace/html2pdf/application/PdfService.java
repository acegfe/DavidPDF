package de.ace.html2pdf.application;

import de.ace.html2pdf.config.DavidPDFException;
import de.ace.html2pdf.model.PdfRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

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
}
