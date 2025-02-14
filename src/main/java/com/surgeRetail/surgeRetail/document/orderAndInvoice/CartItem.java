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
    private BigDecimal unitPrice;  //price for single unit of item final price in item class
    private BigDecimal totalCostPrice;
    private BigDecimal totalBaseSellingPrice;   //total price for all the units sum of final price
    private BigDecimal totalTaxPrice;
    private BigDecimal totalAdditionalPrice;
    private BigDecimal totalDiscountPrice;
    private BigDecimal finalPrice;
}