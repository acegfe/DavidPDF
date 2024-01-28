package de.ace.html2pdf.mixin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class Column {
    private List<Row> rows;
    private Theme theme;

    public Float getHorizontalSize() {
        var maxPrintedString = rows.stream().map(Row::getText)
                .max((o1, o2) -> Float.compare(getWidthPoint(o1), getWidthPoint(o2))).orElse("");
        return getWidthPoint(maxPrintedString);
    }

    public int getVerticalSize() {
        return rows.size();
    }

    private float getWidthPoint(String maxPrintedString) {
        return FooterMapper.getBaseFont(false).getWidthPoint(maxPrintedString, theme.fontSize());
    }
}
