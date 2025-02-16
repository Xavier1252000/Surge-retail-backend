package com.surgeRetail.surgeRetail.document.permissions;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "modules")
public class Modules extends Auditable {
    @Id
    private String id;
    private String name;
    private String tag;
    private String to;
    private String  icon;
    private String parentId;
}
