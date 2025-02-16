package com.surgeRetail.surgeRetail.document.permissions;

import lombok.Data;

import java.util.List;

@Data
public class ModulePermissions {
    private String moduleId;
    private List<String> permissionIds;
}
