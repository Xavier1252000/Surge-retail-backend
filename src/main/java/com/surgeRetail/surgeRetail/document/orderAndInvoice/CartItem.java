package com.surgeRetail.surgeRetail.document.orderAndInvoice;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class CartItem extends Auditable {
    private String itemId;
    private int quantity;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal unitPrice;  //price for single unit of item final price in item class

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalCostPrice;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalBaseSellingPrice;   //total price for all the units sum of final price

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalTaxPrice;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalAdditionalPrice;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalDiscountPrice;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal finalPrice;
}