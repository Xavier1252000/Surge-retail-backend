package com.surgeRetail.surgeRetail.document.master;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Document
public class DiscountMaster extends Auditable {
    @Id
    private String id;
    private String discountName;
    private BigDecimal discountPercentage;
    private String applicableOn; //totalbill, item,
    private String discountCouponCode;

    public static String DISCOUNT_APPLICABLE_ON_ITEM = "Item";
    public static String DISCOUNT_APPLICABLE_ON_TOTAL_BILL = "Total Bill";
}
