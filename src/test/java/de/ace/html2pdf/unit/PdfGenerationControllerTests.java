package de.ace.html2pdf.unit;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.ace.html2pdf.application.PdfRenderComponent;
import de.ace.html2pdf.application.PdfService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

@Test
  void givenHTMLInput_whenAPICalled_thenReturnPDF() throws Exception {
    String input = """
                        <!DOCTYPE html>
                        <html lang="en">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Sample HTML with Footer</title>
                            <style>
                                footer {
                                    background-color: #f4f4f4;
                                    padding: 20px;
                                    text-align: center;
                                }
                            </style>
                        </head>
                        <body>
                            <h1>Welcome to my website</h1>
                            <p>This is a sample HTML page with a footer.</p>
                            <footer>
                                <p>This is the footer section.</p>
                                <p>Contact us: info@example.com</p>
                            </footer>
                        </body>
                        </html>
                    """;

    byte[] byteArray = input.getBytes(StandardCharsets.UTF_8);
    when(pdfService.html2pdf(input)).thenReturn(byteArray);
    this.mockMvc.perform(post("/pdf/html")).andExpect(status().isOk())
        .andExpect(header().string("Content-Type","application/pdf"));
  }
}
