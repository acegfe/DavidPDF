package de.ace.html2pdf.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ApplicationValuesConfig {

    @Value("${security.api.key}")
    private String apiKey;

    @Value("${selenium.driver-path}")
    private String path;
}
