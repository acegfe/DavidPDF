package de.ace.html2pdf.model;

import lombok.Builder;
import lombok.Data;
import org.openqa.selenium.Point;

@Data
@Builder
public class FooterProperties {

    private int height;
    private int width;
    private Point location;
}
