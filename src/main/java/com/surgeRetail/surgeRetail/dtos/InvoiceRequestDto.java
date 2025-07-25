package com.surgeRetail.surgeRetail.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
public class InvoiceRequestDto {

    private Set<String> id_in;
    private Set<Long> serialNo_in;   //invoiceNo
    private Set<String> storeId_in;
    private Set<String> customerId;     //user id who will be buyer
    private BigDecimal grossAmount_gte;     // item total amount without tax
    private BigDecimal grossAmount_lte;
    private BigDecimal netAmount_gte;      //total amount with tax
    private Set<String> invoiceTaxIds_in;
    private BigDecimal invoiceTaxAmount_gte;
    private Set<String> invoiceDiscountIds_in;
    private BigDecimal invoiceDiscountAmount_gte;

    private Set<String> invoiceTender_in;
    private Set<String> deliveryStatus_in;

    private BigDecimal grandTotal_gte;
    private BigDecimal grandTotal_lte;
    private Set<String> paymentStatus_in;    // payment recieved or not
    private Set<String> orderedByType_in;    // ordered by which role

    //    for offline selling from store
    private Set<String> generationType_in;
    private Set<String> customerName_in;
    private Set<String> customerContactNo_in;
    private Instant createdFrom_gte;
    private Instant createdUpTo_lte;
    private Boolean active_is;
}
