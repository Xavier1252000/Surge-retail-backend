package com.surgeRetail.surgeRetail.document.permissions;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "userPermissions")
public class UserPermissions {
    private String id;
    private String userId;
    private List<ModulePermissions> modulesPermissions;
}
