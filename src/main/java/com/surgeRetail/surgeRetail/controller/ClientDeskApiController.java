package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import com.surgeRetail.surgeRetail.security.UserDetailsImpl;
import com.surgeRetail.surgeRetail.service.ClientDeskApiService;
import com.surgeRetail.surgeRetail.utils.requestHandlers.ApiRequestHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//    Permissions for clients to create store-admins, managers, cashiers etc. will be managed by here ie client-desk
@RestController
@RequestMapping("/client-desk")
public class ClientDeskApiController {

    private final ClientDeskApiService clientDeskApiService;

    public ClientDeskApiController(ClientDeskApiService clientDeskApiService){
        this.clientDeskApiService = clientDeskApiService;
    }

    public ApiResponseHandler customRoleBasedUsers(@RequestBody ApiRequestHandler apiRequestHandler){

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

//      checking if client is creating valid and permissible roles
        String superAdminSecret = null;
        Set<String> roles = apiRequestHandler.getSetValue("roles", String.class);
        for (String r:roles){
            if (!clientCanCreateRoles().contains(r))
                return new ApiResponseHandler("Role "+ r +"is invalid, please provide valid roles", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }

        String clientSecret=null;
        if (roles.contains(User.USER_ROLE_CLIENT)){
            clientSecret = apiRequestHandler.getStringValue("clientSecret");
            if (StringUtils.isEmpty(clientSecret))
                return new ApiResponseHandler("please provide clientSecret", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }
        return clientDeskApiService.registerUserWithCustomRoles(firstName, lastName, emailId, mobileNo, username, password, roles, superAdminSecret, clientSecret);
    }

    public static List<String> clientCanCreateRoles(){
        List<String> roles = new ArrayList<>();
        roles.add(User.USER_ROLE_STORE_ADMIN);
        roles.add(User.USER_ROLE_USER);
        roles.add(User.USER_ROLE_MANAGEMENT);
        roles.add(User.USER_ROLE_CASHIER);
        roles.add(User.USER_ROLE_CLIENT);
        return roles;
    }

    private ApiResponseHandler addStore(@RequestBody ApiRequestHandler apiRequestHandler){
        UserDetailsImpl userDetails = (UserDetailsImpl) (SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        String clientId = null;
        if (userDetails!=null || userDetails.getUser().getRoles().contains(User.USER_ROLE_CLIENT)){
            clientId = userDetails.getUser().getId();
        }

        if (StringUtils.isEmpty(clientId)) {        // made because super-admin can access and if check is not applied, store can be registered without clientId
            clientId = apiRequestHandler.getStringValue("clientId");
            if (StringUtils.isEmpty(clientId))
                return new ApiResponseHandler("please provide clientId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }

        String storeName = apiRequestHandler.getStringValue("storeName");
        if (StringUtils.isEmpty(storeName))
            return new ApiResponseHandler("please provide storeName", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String storeContactNo = apiRequestHandler.getStringValue("storeContactNo");
        if (StringUtils.isEmpty(storeContactNo))
            return new ApiResponseHandler("please provide store contactNo", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String registrationNo = apiRequestHandler.getStringValue("registrationNo");
        String taxIdentificationId = apiRequestHandler.getStringValue("taxIdentificationId");
        String taxIdentificationNo = apiRequestHandler.getStringValue("taxIdentificationNo");
        String city = apiRequestHandler.getStringValue("city");
        String state = apiRequestHandler.getStringValue("state");
        String country = apiRequestHandler.getStringValue("country");
        String pinCode = apiRequestHandler.getStringValue("pinCode");
        Set<String> storeAdminIds = apiRequestHandler.getSetValue("storeAdminIds", String.class);

        return clientDeskApiService.addStore(clientId, storeName, storeContactNo, registrationNo, taxIdentificationId, taxIdentificationNo, city, state, country, storeAdminIds, pinCode);
    }
}