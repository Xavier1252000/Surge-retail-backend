package com.surgeRetail.surgeRetail.document.Item;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document
@Data
public class Item {

    @Id
    private String id;
    private String itemName;
    private Double price;
    private String categoryId;
    private List<String> itemImageInfoIds;
    private Integer itemStock;
    private List<String> tutorialLinks;
    private Instant createdOn;
    private Instant modifiedOn;
    private String createdBy;
    private String modifiedBy;
    private boolean active;
}
