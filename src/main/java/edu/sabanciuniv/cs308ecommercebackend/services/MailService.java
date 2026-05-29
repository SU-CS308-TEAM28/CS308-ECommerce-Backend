package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart.CartAction;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class MailService
{
    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:no-reply@teknocs.com}")
    private String fromAddress;

    /**
     * Sends a multipart email with an HTML body and a single PDF attachment.
     * Throws MessagingException if the SMTP send fails — callers decide whether
     * a mail failure should bubble up or be logged-and-swallowed.
     */
    public void sendInvoiceEmail(
            String to,
            String subject,
            String htmlBody,
            byte[] pdfBytes,
            String pdfFilename
    ) throws MessagingException
    {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message, true, StandardCharsets.UTF_8.name());

        helper.setFrom(fromAddress);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true = HTML
        helper.addAttachment(pdfFilename, new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }

    public void sendDiscountNotificationEmail(
            String to,
            String recipientName,
            List<Product> discountedProducts,
            double discount
    ) throws MessagingException
    {
        String thStyle = "padding: 8px 16px; text-align: left; border-bottom: 2px solid #e5e7eb; color: #6b7280; font-size: 13px; font-weight: 600;";
        String tdStyle = "padding: 10px 16px; border-bottom: 1px solid #f3f4f6; font-size: 14px;";

        StringBuilder rows = new StringBuilder();
        for (Product p : discountedProducts)
        {
            double newPrice = p.getPrice() * (1 - discount / 100);
            rows.append("<tr>")
                .append("<td style=\"").append(tdStyle).append("\">").append(p.getName()).append("</td>")
                .append("<td style=\"").append(tdStyle).append("color: #6b7280; text-decoration: line-through;\">₺").append(String.format("%.2f", p.getPrice())).append("</td>")
                .append("<td style=\"").append(tdStyle).append("color: #16a34a; font-weight: 600;\">₺").append(String.format("%.2f", newPrice)).append("</td>")
                .append("</tr>");
        }

        String name = recipientName != null ? recipientName : "there";
        String htmlBody = """
            <div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Helvetica, Arial, sans-serif; max-width: 560px; margin: 0 auto; padding: 24px; color: #111827;">
              <h2 style="margin: 0 0 16px 0; font-size: 22px;">Your wishlist items are on sale!</h2>
              <p style="margin: 0 0 12px 0; line-height: 1.6;">Hi %s,</p>
              <p style="margin: 0 0 20px 0; line-height: 1.6;">Good news! The following items on your wishlist are now on sale with a <strong>%s%% discount</strong>. Don't miss out!</p>
              <table style="width: 100%%; border-collapse: collapse; margin-bottom: 24px;">
                <thead>
                  <tr>
                    <th style="%s">Product</th>
                    <th style="%s">Old Price</th>
                    <th style="%s">New Price</th>
                  </tr>
                </thead>
                <tbody>%s</tbody>
              </table>
              <p style="margin: 24px 0 0 0; color: #6b7280; font-size: 13px;">— The Teknocs team</p>
            </div>
            """.formatted(name, discount, thStyle, thStyle, thStyle, rows);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

        helper.setFrom(fromAddress);
        helper.setTo(to);
        helper.setSubject("Your wishlist items are on sale!");
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }

    public void sendRefundNotificationEmail(
            String to,
            String recipientName,
            List<CartAction.CartProduct> items,
            double totalRefund
    ) throws MessagingException
    {
        String thStyle = "padding: 8px 16px; text-align: left; border-bottom: 2px solid #e5e7eb; color: #6b7280; font-size: 13px; font-weight: 600;";
        String tdStyle = "padding: 10px 16px; border-bottom: 1px solid #f3f4f6; font-size: 14px;";

        StringBuilder rows = new StringBuilder();
        for (CartAction.CartProduct item : items)
            rows.append("<tr>")
                .append("<td style=\"").append(tdStyle).append("\">").append(item.getName()).append("</td>")
                .append("<td style=\"").append(tdStyle).append("\">").append(item.getQuantity()).append("</td>")
                .append("<td style=\"").append(tdStyle).append("color: #16a34a; font-weight: 600;\">₺").append(String.format("%.2f", item.getPrice())).append("</td>")
                .append("</tr>");

        String name = recipientName != null ? recipientName : "there";
        String htmlBody = """
            <div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Helvetica, Arial, sans-serif; max-width: 560px; margin: 0 auto; padding: 24px; color: #111827;">
              <h2 style="margin: 0 0 16px 0; font-size: 22px;">Your refund has been processed!</h2>
              <p style="margin: 0 0 12px 0; line-height: 1.6;">Hi %s,</p>
              <p style="margin: 0 0 20px 0; line-height: 1.6;">Your return has been completed. The following amounts have been reimbursed to your original payment method:</p>
              <table style="width: 100%%; border-collapse: collapse; margin-bottom: 20px;">
                <thead>
                  <tr>
                    <th style="%s">Product</th>
                    <th style="%s">Qty</th>
                    <th style="%s">Refunded</th>
                  </tr>
                </thead>
                <tbody>%s</tbody>
              </table>
              <p style="margin: 0 0 24px 0; font-size: 15px; font-weight: 600;">Total refund: ₺%s</p>
              <p style="margin: 24px 0 0 0; color: #6b7280; font-size: 13px;">— The Teknocs team</p>
            </div>
            """.formatted(name, thStyle, thStyle, thStyle, rows, String.format("%.2f", totalRefund));

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

        helper.setFrom(fromAddress);
        helper.setTo(to);
        helper.setSubject("Your refund has been processed");
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }
}