package com.surgeRetail.surgeRetail.utils;

import com.surgeRetail.surgeRetail.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticatedUserDetails {

    public static UserDetailsImpl getUserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl)authentication.getPrincipal();
    }
}
