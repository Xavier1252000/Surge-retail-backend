package com.surgeRetail.surgeRetail.document.ReturnAndRefund;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Document
@Data
public class ReturnRequest extends Auditable {
    private String id;
    private String userId;       // null in case of offline orders ie purchase from store
    private String invoiceId;
    private Set<ReturnItems> returnItems;
    private BigDecimal totalRefundAmount;
    private String refundStatus;
    private String reason;
    private Set<String> imageUrls;

    public static final String REFUND_STATUS_INITIATED = "Initiated";
    public static final String REFUND_STATUS_VIEWED = "Viewed";
    public static final String REFUND_STATUS_PROCESSED = "Processed";
    public static final String REFUND_STATUS_CANCELLED = "Cancelled";
    public static final String REFUND_STATUS_REFUNDED = "Refunded";

}
