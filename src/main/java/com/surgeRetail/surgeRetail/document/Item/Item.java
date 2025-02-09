package com.surgeRetail.surgeRetail.document.Item;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
public class Item extends Auditable implements Serializable {

    @Id
    private String id;
    private String storeId;
    private String itemName;
    private BigDecimal costPrice; // Purchase price from supplier
    private BigDecimal profitToGainInPercentage;  // will define a base selling price
    private BigDecimal baseSellingPrice; // Base selling price (cost price + personal profit for seller)
    private BigDecimal additionalPrice; // Extra charges like packaging, delivery, etc.
    private Set<String> applicableTaxes; //taxMasterIds,  Tax category (e.g., "GST 18%", "VAT 5%")
    private BigDecimal totalTaxPrice; // Tax amount applied
    private Set<String> discountMasterIds;
    private BigDecimal totalDiscountPrice; // Any discount applied
    private BigDecimal finalPrice; // Computed as (price + additionalPrice + taxPrice - discountPrice)
    private BigDecimal profitMargin; // (FinalPrice - totalTaxPrice - totalDiscountPrice)
    private BigDecimal markupPercentage; // ((profitMargin) / CostPrice) * 100
    private String brand;
    private Set<String> categoryIds; // Reference to the category this item belongs to
    private String supplierId; // Reference to supplier/vendor
    private String description;
    private Set<String> itemImageInfoIds; // List of image IDs for the item
    private Float itemStock; // Available stock count
    private Float stockThreshold; // Minimum stock before restocking is required
    private Set<String> tutorialLinks; // Links to guides or tutorials
    private String skuCode; // Stock Keeping Unit (Unique identifier for inventory)
    private String barcode; // Barcode or QR code for scanning
    private String stockUnit; // Unit of measurement (e.g., "kg", "pcs", "liters")

//    refund and warranty
    private Boolean isReturnable; // Whether the item is returnable or not
    private Boolean isWarrantyAvailable; // Whether a warranty is provided
    private Period warrantyPeriod; // Warranty period (e.g., "6 months", "1 year")
    private Instant expiryDate;
}
