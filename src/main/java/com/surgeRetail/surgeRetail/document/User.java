package com.surgeRetail.surgeRetail.document;

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
    private boolean active;


    public static final String USER_ROLE_USER = "USER";
    public static final String USER_ROLE_ADMIN = "ADMIN";
    public static final String USER_ROLE_SUPER_ADMIN = "SUPER ADMIN";
}


