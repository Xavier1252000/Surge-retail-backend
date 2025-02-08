package com.surgeRetail.surgeRetail.document.orderAndInvoice;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document
public class Invoice extends Auditable {
    @Id
    private String id;
    private String invoiceNumber;
    private String storeId;
    private String customerId;     //user id who will be buyer
    private List<String> invoiceItemsIds;
    private Float netAmount;    //total amount with tax
    private Float grossAmount;   // item total amount without tax
    private Float grandTotal;
    private Float taxAmount;
    private List<String> invoiceTaxIds;
    private Boolean paymentStatus;    // payment recieved or not
    private String invoiceTender;
    private String deliveryStatus;
    private String status;
    private List<String> discountIds;
    private String discountComment;
    private Float totalDiscountAmount;
    private String comment;
    private String orderedByType;    // ordered by which role
//    private Instant createdAt;
//    private Instant updatedAt;
//    private String createdBy;
//    private String updatedBy;
//    private boolean active;


    public static final String PAYMENT_STATUS_PAID = "Paid";
    public static final String PAYMENT_STATUS_PENDING = "Pending";
    public static final String PAYMENT_STATUS_CANCELLED = "Cancelled";

}
