package com.surgeRetail.surgeRetail.document.master;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class UnitMaster {
    @Id
    private String id;
    private String unit;
    private String unitNotation;
}
