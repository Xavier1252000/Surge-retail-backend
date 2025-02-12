package com.surgeRetail.surgeRetail.security.jwt;

import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import lombok.Data;

import java.util.Date;

@Data
public class JwtToken {
    private User user;
    private Date expirationDate;
    private String token;
    private Date creationDate;
}
