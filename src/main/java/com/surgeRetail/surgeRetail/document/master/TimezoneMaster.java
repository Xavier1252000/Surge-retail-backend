package com.surgeRetail.surgeRetail.document.master;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class TimezoneMaster {
    @Id
    private String id;
    private String name;        // E.g., "America/New_York"
    private String offSet;      // E.g., "-05:00"
    private String countryId;
}
