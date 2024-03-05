package de.ace.html2pdf.model;

import lombok.Builder;
import org.openqa.selenium.Point;

public record FooterProperties(int height, int width, Point location) {}

