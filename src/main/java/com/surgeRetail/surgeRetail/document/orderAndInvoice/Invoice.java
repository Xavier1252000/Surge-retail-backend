package com.surgeRetail.surgeRetail.document.orderAndInvoice;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Document
public class Invoice extends Auditable {
    @Id
    private String id;
    private Long serialNo;   //invoiceNo
    private String storeId;
    private String customerId;     //user id who will be buyer

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal grossAmount;     // item total amount without tax

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal netAmount;      //total amount with tax
    private List<String> invoiceTaxIds;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal invoiceTaxAmount;
    private List<String> invoiceDiscountIds;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal invoiceDiscountAmount;
    private String discountComment;

    private InvoiceTender invoiceTender;
    private String deliveryStatus;

    @Field(targetType = FieldType.DECIMAL128)
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
    public static final String PAYMENT_STATUS_PARTIAL = "Partial";
    public static final String PAYMENT_STATUS_CANCELLED = "Cancelled";

    public static final String DELIVERY_STATUS_NOT_DELIVERED = "Not Delivered";
    public static final String DELIVERY_STATUS_OUT_FOR_DELIVERY = "Out For Delivery";
    public static final String DELIVERY_STATUS_DELIVERED = "Delivered";

    public static final String GENERATION_TYPE_ONLINE = "Online";
    public static final String GENERATION_TYPE_OFFLINE = "Off Line";
}
