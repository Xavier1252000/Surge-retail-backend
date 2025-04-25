package com.surgeRetail.surgeRetail.document.permissions;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "modules")
public class Modules extends Auditable {
    @Id
    private String id;
    private String name;
    private String to;
    private short serialNo;
    private String  icon;
    private String parentId;
    private Set<String> canBeAssignedTo;
    private Set<String> canBeAssignedBy;
}
