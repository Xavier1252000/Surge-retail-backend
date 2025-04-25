package com.surgeRetail.surgeRetail.document.userAndRoles;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document
public class SubscriptionDetails {
    @Id
    private String id;
    private String clientId;
    private String plan;
    private Integer storeLimit;   //initially 0, for un subscribed user
    private int totalAmount;
    private String currencyCode;
    private Instant subscriptionFrom;
    private Instant subscriptionTo;
}
