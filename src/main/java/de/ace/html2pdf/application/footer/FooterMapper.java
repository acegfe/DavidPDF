package de.ace.html2pdf.application.footer;

import org.jsoup.nodes.Element;

import java.util.Optional;

public interface FooterMapper {

    /**
     * All the footer gets the same space, align left and vertical centered
     * <footer>
     *     <div> //column 0
     *         <div> //row 0
     *
     *         </div>
     *     </div>
     *     <div> //column 1
     *         <div> //row 0
     *
     *         </div>
     *     </div>
     * </footer>
     */
    public static Footer from(Element footer, Style style, int page) {
        var columns = footer.children().stream().map(
                element -> new Column(
                        element.children().stream().map(
                                element1 -> Optional.ofNullable(element1.text()).orElse("")
                                        .replace("[PAGE_COUNTER]",
                                                String.format("Seite %s von %s", page, style.numberPages()))).toList(), style)).toList();

        return new Footer(columns, style);
    }
}
