package com.surgeRetail.surgeRetail.document.master;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data
@Document
public class TaxMaster extends Auditable {

    @Id
    private String id;
    private String taxType; // GST, VAT, etc.
    private BigDecimal taxPercentage; // Stores tax in decimal (e.g., 18.00 for 18%)
    private String applicableOn; // Item, total bill, etc
    private List<String> applicableStateIds; // If applicable only to a particular state
    private String description; // Additional details about the tax
}
