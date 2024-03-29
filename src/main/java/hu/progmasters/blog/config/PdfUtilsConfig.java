package hu.progmasters.blog.config;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import hu.progmasters.blog.domain.Account;
import hu.progmasters.blog.dto.paypal.CompleteOrder;
import org.springframework.context.annotation.Bean;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PdfUtilsConfig {
    @Bean
    public static ByteArrayOutputStream generatePdfStream(List<Map<String, Object>> queryResults) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        Map<String, Object> firstRow = queryResults.get(0);
        for (String column : firstRow.keySet()) {
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Paragraph paragraph = new Paragraph(column, boldFont);
            document.add(paragraph);
        }
        document.add(new Paragraph("\n"));
        for (Map<String, Object> row : queryResults) {
            for (Object value : row.values()) {
                Paragraph paragraph = new Paragraph(value.toString());
                document.add(paragraph);
            }
            document.add(new Paragraph("\n"));
        }
        document.close();
        return outputStream;
    }

    public static ByteArrayOutputStream generatePdfStreamTwo(CompleteOrder order, Account account, BigDecimal amount) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD | Font.UNDERLINE);
        Paragraph title = new Paragraph("Proof of purchase", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph customerHeader = new Paragraph("\n" + "Customer data:", headerFont);
        document.add(customerHeader);

        addTextToParagraph(document, "  - Name: " + account.getUsername());
        addTextToParagraph(document, "  - E-mail: " + account.getEmail());

        Paragraph addressHeader = new Paragraph("Customer address: ", headerFont);
        document.add(addressHeader);

        addTextToParagraph(document, "  - Cím: ");

        Paragraph productHeader = new Paragraph("Ordered product:", headerFont);
        document.add(productHeader);

        addTextToParagraph(document, "  - Product:  Digify Premium tagság");

        Font OrderFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph paragraph = new Paragraph("Paypal PayId: " + order.getPayId(), OrderFont);
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(paragraph);

        Font totalPriceFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph totalPrice = new Paragraph("Total price: " + amount + " HUF", totalPriceFont);
        totalPrice.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalPrice);

        Font dateFont = new Font(Font.FontFamily.HELVETICA, 12);
        Paragraph date = new Paragraph("Date: " + LocalDate.now().toString(), dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        document.add(date);

        document.close();
        return outputStream;
    }

    private static void addTextToParagraph(Document document, String text) throws DocumentException {
        Font regularFont = new Font(Font.FontFamily.HELVETICA, 12);
        Paragraph paragraph = new Paragraph(text, regularFont);
        document.add(paragraph);
    }

}
