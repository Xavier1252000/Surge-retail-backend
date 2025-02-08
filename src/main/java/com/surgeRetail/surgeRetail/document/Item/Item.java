package com.surgeRetail.surgeRetail.document.Item;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
public class Item extends Auditable implements Serializable {

    @Id
    private String id;
    private String itemName;
    private BigDecimal costPrice; // Purchase price from supplier
    private BigDecimal baseSellingPrice; // Base selling price
    private BigDecimal additionalPrice; // Extra charges like packaging, delivery, etc.
    private List<String> taxMasterIds; // Tax category (e.g., "GST 18%", "VAT 5%")
    private BigDecimal totalTaxPrice; // Tax amount applied
    private List<String> discountMasterIds;
    private BigDecimal totalDiscountPrice; // Any discount applied
    private BigDecimal finalPrice; // Computed as (price + additionalPrice + taxPrice - discountPrice)
    private BigDecimal profitMargin; // (FinalPrice - CostPrice)
    private BigDecimal markupPercentage; // ((FinalPrice - CostPrice) / CostPrice) * 100
    private String brand;
    private String categoryId; // Reference to the category this item belongs to
    private String supplierId; // Reference to supplier/vendor
    private String description;
    private List<String> itemImageInfoIds; // List of image IDs for the item
    private Integer itemStock; // Available stock count
    private Integer stockThreshold; // Minimum stock before restocking is required
    private List<String> tutorialLinks; // Links to guides or tutorials
    private String skuCode; // Stock Keeping Unit (Unique identifier for inventory)
    private String barcode; // Barcode or QR code for scanning
    private String unit; // Unit of measurement (e.g., "kg", "pcs", "liters")
    private Boolean isReturnable; // Whether the item is returnable or not
    private Boolean isWarrantyAvailable; // Whether a warranty is provided
    private Instant warrantyApplicableUpTo;
    private Map<String, Integer> warrantyPeriod; // Warranty period (e.g., "6 months", "1 year")

//    private Instant createdOn;
//    private Instant modifiedOn;
//    private String createdBy;
//    private String modifiedBy;
//    private boolean active; // Whether the item is active and available for sale


    public static final String WARRANTY_PERIOD_YEAR = "Year";
    public static final String WARRANTY_PERIOD_MONTH = "Month";
    public static final String WARRANTY_PERIOD_DAY = "Day";
}
