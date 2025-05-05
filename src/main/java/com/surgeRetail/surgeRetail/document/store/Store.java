package com.surgeRetail.surgeRetail.document.store;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "stores")
public class Store extends Auditable {
    @Id
    private String id;

    // Client who owns/manages the store
    private String clientId;

    // Set of user IDs with admin privileges for this store
    private Set<String> staffIds = new HashSet<>();

    // Store details
    private String storeName;
    private String storeType; // e.g., Retail, Grocery, Electronics
    private String registrationNo; // Business registration number
    private String taxIdentificationId;    //vat, gst, hst etc , for india GST
    private String taxIdentificationNo;                         // for india its gst no
    private String address; // Full address (street, landmark, etc.)
    private String city;
    private String state;
    private String country;
    private String postalCode;

    private Point location;

    private String contactNo; // Primary contact number
    private String email; // Store email for communication
    private String timezone; // e.g., "America/New_York"


    // Operating hours: Map of day (monday-sunday) to open/close times
    private Set<OperatingHours> operatingHours = new HashSet<>();

    // Currency for transactions (ISO 4217 code, e.g., "USD")
    private String currency;

    private transient boolean isStoreOpen;
}
