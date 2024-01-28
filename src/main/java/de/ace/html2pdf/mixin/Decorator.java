package de.ace.html2pdf.mixin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Decorator {
    BOLD("[b]");

    private final String effect;
}
