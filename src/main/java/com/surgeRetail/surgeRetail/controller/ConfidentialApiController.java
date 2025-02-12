package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import com.surgeRetail.surgeRetail.service.ConfidentialApiService;
import com.surgeRetail.surgeRetail.utils.requestHandlers.ApiRequestHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/confidential")
public class ConfidentialApiController {

    private final ConfidentialApiService confidentialApiService;

    public ConfidentialApiController(ConfidentialApiService confidentialApiService){
        this.confidentialApiService = confidentialApiService;
    }

    @PostMapping("/register-user")
    public ApiResponseHandler registerUser(@RequestBody Map<String, Object> requestMap){
        String firstName = (String) requestMap.get("firstName");
        if (StringUtils.isEmpty(firstName)){
            return  new ApiResponseHandler("please provide first name", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }

        String lastName = (String) requestMap.get("lastName");
        if (StringUtils.isEmpty(lastName))
            return new ApiResponseHandler("please provide last name", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String emailId = (String) requestMap.get("emailId");
        if (StringUtils.isEmpty(emailId))
            return new ApiResponseHandler("please provide emailId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String username = (String) requestMap.get("username");
        if (StringUtils.isEmpty(username))
            return new ApiResponseHandler("please provide username", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String mobileNo = (String) requestMap.get("mobileNo");
        if (StringUtils.isEmpty(mobileNo))
            return new ApiResponseHandler("please provide mobileNo", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String password = (String) requestMap.get("password");
        if (StringUtils.isEmpty(password))
            return new ApiResponseHandler("please provide password", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        return confidentialApiService.registerUser(firstName, lastName, username, emailId, mobileNo, password);
    }



    @PostMapping("/register-client")
    public ApiResponseHandler registerClient(@RequestBody ApiRequestHandler apiRequestHandler){
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

        String clientSecret = apiRequestHandler.getStringValue("clientSecret");
        if (StringUtils.isEmpty(clientSecret))
            return new ApiResponseHandler("please provide clientSecret", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        return confidentialApiService.registerClient(firstName, lastName, emailId, mobileNo, username, password, clientSecret);
    }

    @PostMapping("/register-super-user")
    public ApiResponseHandler registerFirstSuperUser(@RequestBody Map<String, Object> requestMap){
        String firstName = (String) requestMap.get("firstName");
        if (StringUtils.isEmpty(firstName)){
            return  new ApiResponseHandler("please provide first name", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }

        String lastName = (String) requestMap.get("lastName");
        if (StringUtils.isEmpty(lastName))
            return new ApiResponseHandler("please provide last name", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String emailId = (String) requestMap.get("emailId");
        if (StringUtils.isEmpty(emailId))
            return new ApiResponseHandler("please provide emailId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String username = (String) requestMap.get("username");
        if (StringUtils.isEmpty(username))
            return new ApiResponseHandler("please provide username", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String mobileNo = (String) requestMap.get("mobileNo");
        if (StringUtils.isEmpty(mobileNo))
            return new ApiResponseHandler("please provide mobileNo", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String password = (String) requestMap.get("password");
        if (StringUtils.isEmpty(password))
            return new ApiResponseHandler("please provide password", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String superAdminSecret = (String) requestMap.get("superAdminSecret");
        if (StringUtils.isEmpty(superAdminSecret))
            return new ApiResponseHandler("please provide superAdminSecret", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        return confidentialApiService.registerSuperUser(firstName, lastName, username, emailId, mobileNo, password, superAdminSecret);
    }

    @PostMapping("register-user-with-custom-roles")
    public ApiResponseHandler registerUserWithCustomRoles(@RequestBody ApiRequestHandler apiRequestHandler){
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
        return confidentialApiService.registerUserWithCustomRoles(firstName, lastName, emailId, mobileNo, username, password,superAdminSecret, clientSecret);
    }
}
