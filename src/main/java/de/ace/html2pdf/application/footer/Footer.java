package de.ace.html2pdf.application.footer;

import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfContentByte;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Footer {
    private List<Column> columns;
    private Style style;

    public void showOn(PdfContentByte over) {
        float minXPosition = style.leftAbsoluteMargin();
        float maxXPosition = style.pageWidth() - style.rightAbsoluteMargin();

        float totalRowXSize = columns.stream().map(Column::getMaxXSize).reduce(0f, Float::sum);
        float totalDrawXSize = maxXPosition - minXPosition;
        float marginBetweenRows = (totalDrawXSize - totalRowXSize) / (columns.size()-1);

        int maxRowYSize = columns.stream().map(Column::getMaxYSize).reduce(0, Integer::max);
        float normalizedMaxYSize = maxRowYSize * style.verticalSpacing();

        //Use the down with above to distribute the size accordingly to the already occupied space (shrink true?)
        //float portionsOfX = ((maxXPosition - style.leftAbsoluteMargin()) / columns.size()) + style.xElementsOffset();

        for (int i = 0; i < columns.size(); i++) {
            var column = columns.get(i);

            float rowYSize = column.getMaxYSize() * style.verticalSpacing();
            float centeringMargin = (normalizedMaxYSize - rowYSize) / 2f;

            float startingY = rowYSize + centeringMargin + style.bottomMargin();
            float startingX = minXPosition + columns.stream().limit(i).map(Column::getMaxXSize).reduce(0f, Float::sum) + (marginBetweenRows*i);

            over.beginText();
            over.setFontAndSize(style.baseFont(), style.fontSize());
            over.setTextMatrix(startingX, startingY);

            for (int x = 0; x < column.getRows().size(); ++x) {
                var row = column.getRows().get(x);
                over.showTextAligned(Element.ALIGN_LEFT, row, startingX, startingY - (x * style.verticalSpacing()), 0);
            }

            over.endText();

        }

    }
}
