package com.surgeRetail.surgeRetail.document.master;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
public class Roles extends Auditable {
    @Id
    private String id;
    private String role;
    private String roleType;


    public final static String ROLES_PRIMARY = "Primary";    //provided by admins
    public final static String ROLES_CUSTOM= "Custom";         //can be created by clients and can be provided to staff

}
