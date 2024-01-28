package de.ace.html2pdf.model.footer;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfReader;
import de.ace.html2pdf.model.FooterStyle;
import lombok.SneakyThrows;
import org.jsoup.nodes.Element;

import java.util.Optional;

public interface FooterMapper {

    /**
     * All the footer gets the same space, align left and vertical centered
     * <footer>
     *     <div> //column 0
     *         <div> //row 0
     *             [PAGE_COUNTER]
     *         </div>
     *     </div>
     *     <div> //column 1
     *         <div> //row 0
     *              [b]Bold text
     *         </div>
     *     </div>
     * </footer>
     */

    static Footer footerFrom(Element footer, Theme theme, int page) {
        var columns = footer.children().stream().map(
                element -> new Column(
                        element.children().stream().map(
                                element1 -> Optional.ofNullable(element1.text()).orElse("")
                                        .replace("[PAGE_COUNTER]",
                                                String.format("Seite %s von %s", page, theme.numberPages()))).toList(), theme)).toList();

        return new Footer(columns, theme);
    }

    @SneakyThrows
    static Theme themeFrom(FooterStyle footerStyle, PdfReader pdfReader, int page) {
        return new Theme(
                BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED),
                footerStyle.fontSize(),
                footerStyle.verticalSpacing(),
                footerStyle.bottomMargin(),
                footerStyle.leftAbsoluteMargin(),
                footerStyle.rightAbsoluteMargin(),
                pdfReader.getPageSize(page).getWidth(),
                pdfReader.getNumberOfPages(),
                0);
    }
}
