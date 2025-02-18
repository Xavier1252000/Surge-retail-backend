package com.surgeRetail.surgeRetail.document.orderAndInvoice;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Document
public class InvoiceItem extends Auditable {

    @Id
    private String id;
    private String itemId;
    private int quantity;
    private BigDecimal pricePerItem;
    private BigDecimal totalPrice;
    private BigDecimal discount; // Discount per item
    private BigDecimal totalDiscount;
    private BigDecimal totalTaxOnItem;
}
