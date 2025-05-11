package com.surgeRetail.surgeRetail.document.userAndRoles;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String emailId;
    private String mobileNo;
    private Set<String> roles = new LinkedHashSet<>();
    private String password;
    private Instant createdOn;
    private Instant modifiedOn;
    private String createdBy;
    private String modifiedBy;
    private boolean active;


    public static final String USER_ROLE_USER = "USER";
    public static final String USER_ROLE_CASHIER = "CASHIER";
    public static final String USER_ROLE_MANAGEMENT = "MANAGEMENT";
    public static final String USER_ROLE_MARKETING = "MARKETING";
    public static final String USER_ROLE_STAFF = "STAFF";
    public static final String USER_ROLE_STORE_ADMIN = "STORE ADMIN";
    public static final String USER_ROLE_CLIENT = "CLIENT";
    public static final String USER_ROLE_SUPER_ADMIN = "SUPER ADMIN";
}


