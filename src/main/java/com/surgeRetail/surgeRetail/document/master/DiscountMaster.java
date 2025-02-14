package com.surgeRetail.surgeRetail.document.master;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@Document
public class DiscountMaster extends Auditable {
    @Id
    private String id;
    private Set<String> storeIds;
    private String discountName;
    private BigDecimal discountPercentage;   //either discountPercentage or discountAmount is required
    private BigDecimal discountAmount;       //other one will be automatically calculated
    private String applicableOn; //totalbill, item,
    private String discountCouponCode;

    public static String DISCOUNT_APPLICABLE_ON_ITEM = "Item";
    public static String DISCOUNT_APPLICABLE_ON_TOTAL_BILL = "Total Bill";
}
