package com.surgeRetail.surgeRetail.document.orderAndInvoice;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Document
public class InvoiceItem {

    @Id
    private String id;
    private String itemId;
    private int quantity;
    private String invoiceId;
    private BigDecimal itemBasePrice;
    private BigDecimal totalBasePrice;
    private Set<String> discountIds;
    private BigDecimal discountPerItem;// Discount per item
    private BigDecimal totalDiscount;
    private Set<String> taxIds;
    private BigDecimal taxPerItem;
    private BigDecimal totalTax;
    private BigDecimal finalPricePerItem;
    private BigDecimal finalPrice;
}
