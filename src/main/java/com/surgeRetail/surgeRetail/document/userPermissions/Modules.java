package com.surgeRetail.surgeRetail.document.userPermissions;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Data
@Document
public class Modules extends Auditable {
    @Id
    private String id;
    private String moduleName;
    private String parentModuleId;
}
