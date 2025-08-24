package com.surgeRetail.surgeRetail.document.orderAndInvoice;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

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

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal itemBasePrice;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalBasePrice;
    private Set<String> discountIds;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal discountPerItem;// Discount per item

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalDiscount;
    private Set<String> taxIds;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal taxPerItem;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalTax;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal finalPricePerItem;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal finalPrice;
}
