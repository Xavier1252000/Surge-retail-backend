package com.surgeRetail.surgeRetail.document.orderAndInvoice;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class CartItem extends Auditable {
    private String itemId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal totalPriceWithoutDiscount;
    private BigDecimal discountPercentage;
    private BigDecimal totalDiscount;
    private BigDecimal totalPriceWithDiscount;
    private Map<String, BigDecimal> additionalChargesForKartItem;
}
