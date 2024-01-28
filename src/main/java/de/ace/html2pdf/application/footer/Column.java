package de.ace.html2pdf.application.footer;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Getter
public class Column {
    private List<String> rows;
    private Style style;

    public Float getMaxXSize() {
        var maxPrintedString = rows.stream().max(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Float.compare(getWidthPoint(o1), getWidthPoint(o2));
            }
        }).orElse("");

        return getWidthPoint(maxPrintedString);
    }

    public int getMaxYSize() {
        return rows.size();
    }

    private float getWidthPoint(String maxPrintedString) {
        return style.baseFont().getWidthPoint(maxPrintedString, style.fontSize());
    }


}
