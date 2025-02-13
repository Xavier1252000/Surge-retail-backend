package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import com.surgeRetail.surgeRetail.service.ClientDeskApiService;
import com.surgeRetail.surgeRetail.utils.requestHandlers.ApiRequestHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//    Permissions for clients to create store-admins, managers, cashiers etc. will be managed by here ie client-desk
@RestController
@RequestMapping("/client-desk")
public class ClientDeskApiController {

    private final ClientDeskApiService clientDeskApiService;

    public ClientDeskApiController(ClientDeskApiService clientDeskApiService){
        this.clientDeskApiService = clientDeskApiService;
    }

    public ApiResponseHandler customStoreBasedUsers(@RequestBody ApiRequestHandler apiRequestHandler){
        String firstName = apiRequestHandler.getStringValue("firstName");
        if (StringUtils.isEmpty(firstName))
            return new ApiResponseHandler("please provide firstName", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String lastName = apiRequestHandler.getStringValue("lastName");
        if (StringUtils.isEmpty(lastName))
            return new ApiResponseHandler("please provide lastName", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String emailId = apiRequestHandler.getStringValue("emailId");
        if (StringUtils.isEmpty(emailId))
            return new ApiResponseHandler("please provide emailId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String mobileNo = apiRequestHandler.getStringValue("mobileNo");
        if (StringUtils.isEmpty(mobileNo))
            return new ApiResponseHandler("please provide mobileNo", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String password = apiRequestHandler.getStringValue("password");
        if (StringUtils.isEmpty(password))
            return new ApiResponseHandler("please provide password", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String username = apiRequestHandler.getStringValue("username");
        if (StringUtils.isEmpty(username))
            return new ApiResponseHandler("please provide username", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String superAdminSecret = null;
        List<String> roles = apiRequestHandler.getListValue("roles", String.class);
        if (roles.contains(User.USER_ROLE_SUPER_ADMIN)) {
            superAdminSecret = apiRequestHandler.getStringValue("superAdminSecret");
            if (StringUtils.isEmpty(superAdminSecret))
                return new ApiResponseHandler("please provide superAdminSecret", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }

        String clientSecret=null;
        if (roles.contains(User.USER_ROLE_CLIENT)){
            clientSecret = apiRequestHandler.getStringValue("clientSecret");
            if (StringUtils.isEmpty(clientSecret))
                return new ApiResponseHandler("please provide clientSecret", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }
        return clientDeskApiService.registerUserWithCustomRoles(firstName, lastName, emailId, mobileNo, username, password,superAdminSecret, clientSecret);
    }

}
