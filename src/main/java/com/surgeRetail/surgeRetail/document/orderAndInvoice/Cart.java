package com.surgeRetail.surgeRetail.document.orderAndInvoice;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Document
public class Cart extends Auditable {
    @Id
    private String id;
    private String customerId;    //user customer id
    private List<CartItem> cartItems;
    private BigDecimal totalPriceWithoutDiscount;
    private BigDecimal discountPercentage;
    private BigDecimal totalDiscount;
    private BigDecimal totalPriceWithDiscount;    //price with discount
    private String sessionId;   //for guest users
    private Map<String, BigDecimal> additionalChargesForKart;
}
