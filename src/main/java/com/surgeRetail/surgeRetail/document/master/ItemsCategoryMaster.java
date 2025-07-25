package com.surgeRetail.surgeRetail.document.master;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@EqualsAndHashCode(callSuper = false)
@Document
@Data
public class ItemsCategoryMaster extends Auditable {

    private String id;
    private Set<String> storeIds;
    private String categoryName;
    private String parentCategoryId;
    private String description;
}
