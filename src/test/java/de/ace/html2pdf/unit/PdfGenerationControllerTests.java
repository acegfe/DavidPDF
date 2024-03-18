package de.ace.html2pdf.unit;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.ace.html2pdf.application.PdfRenderComponent;
import de.ace.html2pdf.application.PdfService;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@AutoConfigureMockMvc
public class PdfGenerationControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PdfService pdfService;

  @MockBean
  private PdfRenderComponent pdfRenderComponent;

  @Value("classpath:input.html")
  private Resource inputFile;

  @Test
  void shouldReturnApplicationHealth() throws Exception {
    this.mockMvc.perform(get("/pdf/healthCheck")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  void givenHTMLInput_whenAPICalled_thenReturnPDF() throws Exception {
    String input = inputFile.getContentAsString(StandardCharsets.UTF_8);

    byte[] byteArray = input.getBytes(StandardCharsets.UTF_8);
    when(pdfService.html2pdf(input)).thenReturn(byteArray);
    this.mockMvc.perform(post("/pdf/html")
                .content(input))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type","application/pdf"));
  }
}
