package com.surgeRetail.surgeRetail.document.Item;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document
@Data
public class ItemsCategoryMaster {

    @Id
    private String id;
    private String categoryName;
    private Instant createdOn;
    private Instant modifiedOn;
    private boolean active;
}
