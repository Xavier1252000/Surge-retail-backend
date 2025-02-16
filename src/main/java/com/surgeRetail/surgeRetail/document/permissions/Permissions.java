package com.surgeRetail.surgeRetail.document.permissions;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "permissions")
public class Permissions extends Auditable {
    @Id
    private String id;
    private String name;
    private String description;
}
