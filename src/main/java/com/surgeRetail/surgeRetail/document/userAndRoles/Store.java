package com.surgeRetail.surgeRetail.document.userAndRoles;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document
public class Store extends Auditable {
    @Id
    private String id;
    private String clientId;
    private String storeAdminId;
    private String storeName;
    private String storeContactNo;
    private String registrationNo;
    private String gstNo;
    private String city;
    private String state;
    private String country;
}
