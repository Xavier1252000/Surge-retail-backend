package com.surgeRetail.surgeRetail.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "superAdminInfo")
public class SuperAdminInfo {
    @Id
    private String id;
    private String userId;
    private String superAdminSecret;
    private Instant createdOn;
    private Instant modifiedOn;
    private boolean active;
}