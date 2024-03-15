package de.ace.html2pdf;

import static org.assertj.core.api.Assertions.assertThat;

import de.ace.html2pdf.controller.PdfGenerationController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
public class MagicApplicationTests {

  @Autowired
  PdfGenerationController controller;

  @Test
  void contextLoads() {
    assertThat(controller).isNotNull();
  }

}
