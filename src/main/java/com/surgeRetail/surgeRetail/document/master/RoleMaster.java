package com.surgeRetail.surgeRetail.document.master;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@EqualsAndHashCode(callSuper = true)
public class RoleMaster extends Auditable {
    @Id
    private String id;
    private String role;
    private String description;
}
