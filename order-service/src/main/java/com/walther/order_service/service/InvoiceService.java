package com.walther.order_service.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.walther.order_service.model.entities.Order;
import com.walther.order_service.model.entities.OrderItem;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class InvoiceService {

    public ByteArrayInputStream generateInvoice(Order order) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        Paragraph header = new Paragraph("Invoice");
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        Paragraph orderNumber = new Paragraph("Order Number: " + order.getNumberOrder());
        document.add(orderNumber);

        for (OrderItem item : order.getOrderItems()) {
            Paragraph itemParagraph = new Paragraph("SKU: " + item.getSku() + ", Quantity: " + item.getQuantity() + ", Price: " + item.getPrice() + ", Importe: " + item.getImporte());
            document.add(itemParagraph);
        }

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}
