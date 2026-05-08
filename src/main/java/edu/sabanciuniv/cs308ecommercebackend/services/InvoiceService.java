package edu.sabanciuniv.cs308ecommercebackend.services;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import edu.sabanciuniv.cs308ecommercebackend.models.Order;
import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart.CartAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Service
public class InvoiceService
{
    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);
    private static final Locale TR = new Locale("tr", "TR");

    // Palette mirrors the frontend (#111827 ink, #6b7280 muted, #e5e7eb border)
    private static final Color INK    = new Color(17, 24, 39);
    private static final Color MUTED  = new Color(107, 114, 128);
    private static final Color BORDER = new Color(229, 231, 235);

    public byte[] generateInvoicePdf(Order order, User user, List<CartAction.CartProduct> products)
            throws DocumentException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 56, 56, 56, 56);
        PdfWriter.getInstance(document, baos);
        document.open();

        addHeader(document, order);
        addThankYou(document);
        addOrderMeta(document, order, user);
        addProductsTable(document, products);
        addTotal(document, order.getTotalPrice());
        addFooter(document);

        document.close();
        return baos.toByteArray();
    }

    // -------------------------------------------------------------------------
    // Sections
    // -------------------------------------------------------------------------

    private void addHeader(Document document, Order order) throws DocumentException
    {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{2f, 1f});

        // Logo on the left
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        try
        {
            ClassPathResource res = new ClassPathResource("static/logo.png");
            Image logo = Image.getInstance(res.getInputStream().readAllBytes());
            logo.scaleToFit(140f, 60f);
            logoCell.addElement(logo);
        }
        catch (Exception e)
        {
            log.warn("Logo not found at static/logo.png; falling back to text mark");
            Paragraph mark = new Paragraph("TEKNOCS",
                    new Font(Font.HELVETICA, 24f, Font.BOLD, INK));
            logoCell.addElement(mark);
        }
        header.addCell(logoCell);

        // Invoice label + short id on the right
        PdfPCell metaCell = new PdfPCell();
        metaCell.setBorder(Rectangle.NO_BORDER);

        Paragraph invoice = new Paragraph("INVOICE",
                new Font(Font.HELVETICA, 22f, Font.BOLD, INK));
        invoice.setAlignment(Element.ALIGN_RIGHT);
        metaCell.addElement(invoice);

        Paragraph idLine = new Paragraph(shortId(order.getId()),
                new Font(Font.HELVETICA, 12f, Font.NORMAL, MUTED));
        idLine.setAlignment(Element.ALIGN_RIGHT);
        metaCell.addElement(idLine);

        header.addCell(metaCell);
        document.add(header);
    }

    private void addThankYou(Document document) throws DocumentException
    {
        Paragraph title = new Paragraph("Thank you for your purchase!",
                new Font(Font.HELVETICA, 18f, Font.BOLD, INK));
        title.setSpacingBefore(28f);
        title.setSpacingAfter(6f);
        document.add(title);

        Paragraph sub = new Paragraph(
                "We're preparing your order. Here's a summary of your purchase.",
                new Font(Font.HELVETICA, 11f, Font.NORMAL, MUTED));
        sub.setSpacingAfter(24f);
        document.add(sub);
    }

    private void addOrderMeta(Document document, Order order, User user) throws DocumentException
    {
        SimpleDateFormat fmt = new SimpleDateFormat("dd MMMM yyyy", TR);

        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        t.setSpacingAfter(20f);

        t.addCell(metaCell("ORDER DATE",
                order.getOrderDate() != null ? fmt.format(order.getOrderDate()) : "—"));
        t.addCell(metaCell("BILLED TO", fullName(user)));
        t.addCell(metaCell("EMAIL", user.getEmail() != null ? user.getEmail() : "—"));
        t.addCell(metaCell("DELIVERY ADDRESS",
                order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "—"));

        document.add(t);
    }

    private void addProductsTable(Document document, List<CartAction.CartProduct> products)
            throws DocumentException
    {
        PdfPTable t = new PdfPTable(new float[]{4f, 1f, 1.5f, 1.5f});
        t.setWidthPercentage(100);
        t.setSpacingAfter(16f);

        Font headerFont = new Font(Font.HELVETICA, 9f, Font.BOLD, MUTED);
        t.addCell(headerCell("PRODUCT",  headerFont, Element.ALIGN_LEFT));
        t.addCell(headerCell("QTY",      headerFont, Element.ALIGN_CENTER));
        t.addCell(headerCell("PRICE",    headerFont, Element.ALIGN_RIGHT));
        t.addCell(headerCell("SUBTOTAL", headerFont, Element.ALIGN_RIGHT));

        Font rowFont = new Font(Font.HELVETICA, 11f, Font.NORMAL, INK);
        NumberFormat tl = currencyFormat();

        for (CartAction.CartProduct p : products)
        {
            double unitPrice = p.getPrice() * (1.0 - p.getActiveDiscount() / 100.0);
            double subtotal  = unitPrice * p.getQuantity();

            t.addCell(bodyCell(p.getName(),                            rowFont, Element.ALIGN_LEFT));
            t.addCell(bodyCell(String.valueOf(p.getQuantity()),        rowFont, Element.ALIGN_CENTER));
            t.addCell(bodyCell("₺" + tl.format(unitPrice),             rowFont, Element.ALIGN_RIGHT));
            t.addCell(bodyCell("₺" + tl.format(subtotal),              rowFont, Element.ALIGN_RIGHT));
        }

        document.add(t);
    }

    private void addTotal(Document document, double total) throws DocumentException
    {
        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        t.setWidths(new float[]{2f, 1f});

        PdfPCell empty = new PdfPCell();
        empty.setBorder(Rectangle.NO_BORDER);
        t.addCell(empty);

        PdfPCell totalCell = new PdfPCell();
        totalCell.setBorder(Rectangle.NO_BORDER);

        Paragraph label = new Paragraph("TOTAL",
                new Font(Font.HELVETICA, 10f, Font.BOLD, MUTED));
        label.setAlignment(Element.ALIGN_RIGHT);
        label.setSpacingAfter(4f);
        totalCell.addElement(label);

        Paragraph value = new Paragraph("₺" + currencyFormat().format(total),
                new Font(Font.HELVETICA, 22f, Font.BOLD, INK));
        value.setAlignment(Element.ALIGN_RIGHT);
        totalCell.addElement(value);

        t.addCell(totalCell);
        document.add(t);
    }

    private void addFooter(Document document) throws DocumentException
    {
        Paragraph footer = new Paragraph(
                "Questions about this order? Reply to this email and we'll help.",
                new Font(Font.HELVETICA, 9f, Font.NORMAL, MUTED));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(48f);
        document.add(footer);
    }

    // -------------------------------------------------------------------------
    // Cell helpers
    // -------------------------------------------------------------------------

    private PdfPCell metaCell(String label, String value)
    {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(12f);

        Paragraph labelP = new Paragraph(label,
                new Font(Font.HELVETICA, 9f, Font.BOLD, MUTED));
        labelP.setSpacingAfter(2f);
        cell.addElement(labelP);

        Paragraph valueP = new Paragraph(value,
                new Font(Font.HELVETICA, 11f, Font.NORMAL, INK));
        cell.addElement(valueP);

        return cell;
    }

    private PdfPCell headerCell(String text, Font font, int align)
    {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setHorizontalAlignment(align);
        c.setBorder(Rectangle.BOTTOM);
        c.setBorderColor(BORDER);
        c.setPaddingTop(8f);
        c.setPaddingBottom(8f);
        return c;
    }

    private PdfPCell bodyCell(String text, Font font, int align)
    {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setHorizontalAlignment(align);
        c.setBorder(Rectangle.BOTTOM);
        c.setBorderColor(BORDER);
        c.setPaddingTop(12f);
        c.setPaddingBottom(12f);
        return c;
    }

    // -------------------------------------------------------------------------
    // Misc
    // -------------------------------------------------------------------------

    private static NumberFormat currencyFormat()
    {
        NumberFormat tl = NumberFormat.getNumberInstance(TR);
        tl.setMinimumFractionDigits(2);
        tl.setMaximumFractionDigits(2);
        return tl;
    }

    private static String fullName(User user)
    {
        String n = user.getName()    == null ? "" : user.getName();
        String s = user.getSurname() == null ? "" : user.getSurname();
        String full = (n + " " + s).trim();
        return full.isEmpty() ? "—" : full;
    }

    private static String shortId(String id)
    {
        if (id == null || id.isEmpty()) return "#";
        return "#" + id.substring(Math.max(0, id.length() - 6)).toUpperCase();
    }
}