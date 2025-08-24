package com.surgeRetail.surgeRetail.document.Item;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
public class Item extends Auditable implements Serializable {
    @Id
    private String id;
    private String storeId;
    private String itemName;
    private Integer skuCode; // Stock Keeping Unit (Unique identifier for inventory)

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal costPrice; // Purchase price from supplier

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal profitToGainInPercentage;  // will define a base selling price

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal baseSellingPrice; // Base selling price (cost price + personal profit for seller)

    @Field(targetType= FieldType.DECIMAL128)
    private BigDecimal additionalPrice; // Extra charges like packaging, delivery, etc.
    private Set<String> applicableTaxes; //taxMasterIds,  Tax category (e.g., "GST 18%", "VAT 5%")

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalTaxPrice = BigDecimal.ZERO; // Tax amount applied
    private Set<String> discountMasterIds;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalDiscountPrice = BigDecimal.ZERO; // Any discount applied

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal finalPrice = BigDecimal.ZERO; // Computed as (price + additionalPrice + taxPrice - discountPrice)

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal profitMargin; // (FinalPrice - totalTaxPrice - costPrice)

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal markupPercentage; // ((profitMargin) / CostPrice) * 100
    private String brand;
    private Set<String> categoryIds; // Reference to the category this item belongs to
    private String supplierId; // Reference to supplier/vendor
    private String description;
    private List<String> itemImageInfoIds; // List of image IDs for the item
    private Float itemStock; // Available stock count
    private Float stockThreshold; // Minimum stock before restocking is required+
    private Set<String> tutorialLinks; // Links to guides or tutorials
    private String barcode; // Barcode or QR code for scanning
    private String stockUnit; // Unit of measurement (e.g., "kg", "pcs", "liters")
    private Integer thresholdQuantityForAddTax;  //some taxes are applicable for buying or selling more than n no of units, this field will hold that quantity after that the tax will be applicable
//    refund and warranty
    private Boolean isReturnable; // Whether the item is returnable or not
    private Boolean isWarrantyAvailable; // Whether a warranty is provided
    private Period warrantyPeriod; // Warranty period (e.g., "6 months", "1 year")
    private Instant expiryDate;
}
