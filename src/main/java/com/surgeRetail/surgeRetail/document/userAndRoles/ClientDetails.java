package com.surgeRetail.surgeRetail.document.userAndRoles;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document
public class ClientDetails extends Auditable {
    private String id;
    private String userId;
    private int numericId;   //auto-increment  apart  from the user ids
    private String displayName;
    private String secondaryEmail;
    private String alternateContactNo;
    private boolean subscriptionStatus;   //by default false
    private SubscriptionDetails currentSubscriptionDetails;
    private String logoUrl;
    private String languagePreference;
    private String timeZone;
    private String businessRegistrationNo;
    private String businessType;
    private String country;
    private String state;
    private String city;
    private String postalCode;
    private String address;
}
