package de.ace.html2pdf.mixin;

import com.lowagie.text.pdf.PdfContentByte;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Footer {
    private List<Column> columns;
    private Theme theme;

    public void operate(PdfContentByte over) {
        float minXPosition = theme.leftAbsoluteMargin();
        float maxXPosition = theme.pageWidth() - theme.rightAbsoluteMargin();

        float totalRowXSize = columns.stream().map(Column::getHorizontalSize).reduce(0f, Float::sum);
        float totalDrawXSize = maxXPosition - minXPosition;
        float marginBetweenRows = (totalDrawXSize - totalRowXSize) / (columns.size() - 1);

        int maxRowYSize = columns.stream().map(Column::getVerticalSize).reduce(0, Integer::max);
        float normalizedMaxYSize = maxRowYSize * theme.verticalSpacing();

        //Use the down with above to distribute the size accordingly to the already occupied space (shrink true?)
        //float portionsOfX = ((maxXPosition - style.leftAbsoluteMargin()) / columns.size()) + style.xElementsOffset();

        for (int i = 0; i < columns.size(); i++) {
            var column = columns.get(i);

            float rowYSize = column.getVerticalSize() * theme.verticalSpacing();
            float centeringMargin = (normalizedMaxYSize - rowYSize) / 2f;

            float startingY = rowYSize + centeringMargin + theme.bottomMargin();
            float startingX = minXPosition + columns.stream().limit(i)
                    .map(Column::getHorizontalSize).reduce(0f, Float::sum) + (marginBetweenRows * i);

            for (int x = 0; x < column.getRows().size(); ++x) {
                var row = column.getRows().get(x);
                row.operate(over, startingX, startingY - (x * theme.verticalSpacing()));
            }

        }

    }
}
