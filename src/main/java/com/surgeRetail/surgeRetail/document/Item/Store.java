package com.surgeRetail.surgeRetail.document.Item;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Document
public class Store extends Auditable {
    @Id
    private String id;
    private String clientId;
    private Set<String> storeAdminIds = new HashSet<>();
    private String storeName;
    private String storeContactNo;
    private String registrationNo;
    private String gstNo;
    private String pinCode;
    private String city;
    private String state;
    private String country;
}
