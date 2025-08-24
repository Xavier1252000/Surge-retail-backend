package com.surgeRetail.surgeRetail.document.ReturnAndRefund;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

@Data
public class ReturnItems {
    private String invoiceItemId;
    private int quantity ;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal refundAmount;
}
