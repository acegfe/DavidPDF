package de.ace.html2pdf.unit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.ace.html2pdf.application.PdfRenderComponent;
import de.ace.html2pdf.application.PdfService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

  @Test
  void shouldReturnApplicationHealth() throws Exception {
    this.mockMvc.perform(get("/pdf/healthCheck")).andDo(print()).andExpect(status().isOk());
  }

}
