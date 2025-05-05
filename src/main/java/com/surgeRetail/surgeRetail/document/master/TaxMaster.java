package com.surgeRetail.surgeRetail.document.master;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Document
@ToString
public class TaxMaster extends Auditable {

    @Id
    private String id;
    private Set<String> storeIds;
    private String taxCode;  //SGST, IGST, GST-18
    private String taxType; // GST, VAT, etc.
    private BigDecimal taxPercentage; // Stores tax in decimal (e.g., 18.00 for 18%)
    private String applicableOn; // Item, total bill, etc
    private Set<String> applicableStateIds = new HashSet<>(); // If applicable only to a particular state
    private Set<String> applicableCategories = new HashSet<>();
    private Boolean inclusionOnBasePrice;  // whether tax is included on base price or will be applicable over base price;
    private String description; // Additional details about the tax

    public final static String APPLICABLE_ON_ITEM = "Item";
    public final static String APPLICABLE_ON_INVOICE = "Invoice";
    public final static String APPLICABLE_ON_STORE = "Store";
    public final static String APPLICABLE_OVER_SPECIFIC_QUANTITY = "Over Specific Quantity";
}
