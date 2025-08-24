package com.surgeRetail.surgeRetail.document.orderAndInvoice;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Document
public class Cart extends Auditable {
    @Id
    private String id;
    private String customerId;    //user customer id
    private List<CartItem> cartItems = new ArrayList<>();
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalPriceBeforeDiscount;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal discountPercentage;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal taxOnFinalPrice;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalDiscount;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalPriceWithDiscount;    //price with discount
    private String sessionId;   //for guest users
}
