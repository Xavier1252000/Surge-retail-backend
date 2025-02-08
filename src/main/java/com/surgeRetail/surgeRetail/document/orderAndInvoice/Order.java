package com.surgeRetail.surgeRetail.document.orderAndInvoice;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data
@Document
public class Order extends Auditable {
    @Id
    private String id;
    private String orderNumber;
    private String customerId;
    private String customerEmailId;
    private String customerMobileNo;
    private List<String> orderItemIds;
    private BigDecimal totalAmount;
    private String orderStatus;
    private ShippingAddress shippingAddress;
}
