package com.surgeRetail.surgeRetail.document.orderAndInvoice;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Document
public class Order extends Auditable {
    @Id
    private String id;
    private String orderNumber;
    private String customerId;    //user id of buyer
    private String customerFullName;
    private String customerEmailId;
    private String customerMobileNo;
    private Set<String> itemIds;
    private BigDecimal totalBasePrice;
    private BigDecimal totalTaxPrice;
    private BigDecimal totalDiscount;
    private BigDecimal finalAmount;
    private String orderStatus;
    private ShippingAddress shippingAddress;
    public boolean acceptedTerms;


    public static final String ORDER_STATUS_REQUESTED = "Requested";
    public static final String ORDER_STATUS_REVIEWED = "Reviewed";
    public static final String ORDER_STATUS_PLACED = "Placed";
    public static final String ORDER_STATUS_OUT_FOR_DELIVERY = "Out for delivery";
    public static final String ORDER_STATUS_DELIVERED = "Delivered";
    public static final String ORDER_STATUS_CANCELLED = "Cancelled";
}
