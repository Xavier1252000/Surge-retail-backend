package com.surgeRetail.surgeRetail.document.orderAndInvoice;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class InvoiceTender {
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal pendingAmount;
    private Map<String, BigDecimal> paymentModes = new HashMap<>();
}
