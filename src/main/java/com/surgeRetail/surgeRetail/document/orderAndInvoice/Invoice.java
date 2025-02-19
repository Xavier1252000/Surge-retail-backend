package com.surgeRetail.surgeRetail.document.orderAndInvoice;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data
@Document
public class Invoice extends Auditable {
    @Id
    private String id;
    private Long serialNo;   //invoiceNo
    private String storeId;
    private String customerId;     //user id who will be buyer
    private List<String> invoiceItemsIds;
    private BigDecimal grossAmount;   // item total amount without tax
    private BigDecimal netAmount;    //total amount with tax
    private List<String> invoiceTaxIds;
    private BigDecimal taxAmount;

    private String invoiceTender;
    private String deliveryStatus;
    private String status;
    private List<String> discountIds;
    private String discountComment;
    private Float totalDiscountAmount;

    private BigDecimal grandTotal;
    private String paymentStatus;    // payment recieved or not
    private String comment;
    private String orderedByType;    // ordered by which role

//    for offline selling from store
    private String generationType;
    private String customerName;
    private String customerContactNo;



    public static final String PAYMENT_STATUS_PAID = "Paid";
    public static final String PAYMENT_STATUS_PENDING = "Pending";
    public static final String PAYMENT_STATUS_CANCELLED = "Cancelled";

    public static final String GENERATION_TYPE_ONLINE = "Online";
    public static final String GENERATION_TYPE_OFFLINE = "Off Line";


}
