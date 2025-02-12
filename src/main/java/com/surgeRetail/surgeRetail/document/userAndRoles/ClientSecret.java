package com.surgeRetail.surgeRetail.document.userAndRoles;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document
public class ClientSecret extends Auditable {
    private String id;
    private String userId;
    private String clientSecret;
}
