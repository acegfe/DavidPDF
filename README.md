# DavidPDF - HTML2PDF by Spring Boot, Selenium & Docker
# README.md for HTML to PDF API

## Overview
This document provides a guide for using the HTML to PDF conversion API hosted at `http://address:8080/pdf/html`. This API allows you to convert HTML content to a PDF document, with customizable footer styles.

## API Endpoint
**URL:** `http://address:8080/pdf/html`

## HTTP Method
**POST**

## Headers
- **Authorization**: `Bearer apiKey`

## Request Body
The body of the request should be a JSON object with the following keys:

- **html**: A string containing the HTML content to be converted.
- **footerStyle**: An object describing the style of the footer in the PDF. It contains the following properties:
    - **fontSize**: The font size for the footer text (integer).
    - **verticalSpacing**: The vertical spacing of the footer content (integer).
    - **bottomMargin**: The bottom margin of the footer (integer).
    - **leftAbsoluteMargin**: The left margin for the footer (integer).
    - **rightAbsoluteMargin**: The right margin for the footer (integer).

## Footer HTML Structure
When including a footer in the HTML, it should be structured as follows:

```html
/**
 * All the footer gets the same space, align left and vertical centered
 * <footer>
 *     <div> <!-- column 0 -->
 *         <div> <!-- row 0 -->
 *             [PAGE_COUNTER]
 *         </div>
 *     </div>
 *     <div> <!-- column 1 -->
 *         <div> <!-- row 0 -->
 *              [b]Bold text
 *         </div>
 *     </div>
 * </footer>
 */
```

## Usage Example

### JSON Request Body Example
```json
{
  "html": "<h1>Your HTML Content Here</h1>",
  "footerStyle": {
    "fontSize": 6,
    "verticalSpacing": 6,
    "bottomMargin": 25,
    "leftAbsoluteMargin": 50,
    "rightAbsoluteMargin": 50
  }
}
```

### cURL Example
```bash
curl --location 'http://address:8080/pdf/html' \
--header 'Authorization: Bearer apiKey' \
--header 'Content-Type: application/json' \
--data-raw '{
  "html" : "",  
  "footerStyle": {
    "fontSize" : 6,
    "verticalSpacing" : 6,
    "bottomMargin" : 25,
    "leftAbsoluteMargin" : 50,
    "rightAbsoluteMargin" : 50
  }
}'
```


## Notes
- The `[PAGE_COUNTER]` in the footer will be automatically replaced by the current page number in the generated PDF.
- The `[b]` tag in the footer will make the following text bold.
- Ensure that the HTML content is properly encoded and escaped to avoid JSON errors.

---

**End of README.md**










