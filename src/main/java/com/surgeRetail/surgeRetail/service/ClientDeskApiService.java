package com.surgeRetail.surgeRetail.service;


import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.springframework.stereotype.Service;

@Service
public class ClientDeskApiService {
    public ApiResponseHandler registerUserWithCustomRoles(String firstName, String lastName, String emailId, String mobileNo, String username, String password, String superAdminSecret, String clientSecret) {
        return new ApiResponseHandler("registered with role"+"  ", null, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }
}
