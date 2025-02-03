package com.surgeRetail.surgeRetail.document.Item;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document
public class ItemImageInfo {

    @Id
    private String id;
    private String imageUrl;
    private Instant createdOn;
    private Instant modifiedOn;
    private boolean active;
}
