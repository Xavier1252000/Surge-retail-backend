package com.surgeRetail.surgeRetail.document.Item;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Store {
    @Id
    private String id;
    private String storeName;
    private String userId;
}
