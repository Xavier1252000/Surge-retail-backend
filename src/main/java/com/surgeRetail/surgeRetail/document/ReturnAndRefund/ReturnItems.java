package com.surgeRetail.surgeRetail.document.ReturnAndRefund;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReturnItems {
    private String invoiceItemId;
    private int quantity ;
    private BigDecimal refundAmount;
}
