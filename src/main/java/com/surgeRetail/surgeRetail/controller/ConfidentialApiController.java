package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import com.surgeRetail.surgeRetail.service.ConfidentialApiService;
import com.surgeRetail.surgeRetail.utils.requestHandlers.ApiRequestHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

//    Apis can only have access to the super admins
@RestController
@RequestMapping("/confidential")
public class ConfidentialApiController {

    private final ConfidentialApiService confidentialApiService;

    public ConfidentialApiController(ConfidentialApiService confidentialApiService){
        this.confidentialApiService = confidentialApiService;
    }

    @PostMapping("/create-roles")
    public ApiResponseHandler createRoles(@RequestBody ApiRequestHandler apiRequestHandler){
        String role = apiRequestHandler.getStringValue("role");
        if (StringUtils.isEmpty(role))
            return new ApiResponseHandler("please provide role", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String description = apiRequestHandler.getStringValue("description");
        return confidentialApiService.createRole(role, description);
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
    public ResponseEntity<ApiResponseHandler> registerClient(@RequestBody ApiRequestHandler apiRequestHandler){
        String firstName = apiRequestHandler.getStringValue("firstName");
        if (StringUtils.isEmpty(firstName))
            return new ResponseEntity<>(new ApiResponseHandler("please provide firstName", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String lastName = apiRequestHandler.getStringValue("lastName");
        if (StringUtils.isEmpty(lastName))
            return new ResponseEntity<>(new ApiResponseHandler("please provide lastName", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String emailId = apiRequestHandler.getStringValue("emailId");
        if (StringUtils.isEmpty(emailId))
            return new ResponseEntity<>(new ApiResponseHandler("please provide emailId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String mobileNo = apiRequestHandler.getStringValue("mobileNo");
        if (StringUtils.isEmpty(mobileNo))
            return new ResponseEntity<>(new ApiResponseHandler("please provide mobileNo", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String password = apiRequestHandler.getStringValue("password");
        if (StringUtils.isEmpty(password))
            return new ResponseEntity<>(new ApiResponseHandler("please provide password", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String username = apiRequestHandler.getStringValue("username");
        if (StringUtils.isEmpty(username))
            return new ResponseEntity<>(new ApiResponseHandler("please provide username", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(confidentialApiService.registerClient(firstName, lastName, emailId, mobileNo, username, password), HttpStatus.CREATED);
    }

    @PostMapping("/add-client-details")
    public ResponseEntity<ApiResponseHandler> addClientDetails(@RequestBody ApiRequestHandler apiRequestHandler){
        String userId = apiRequestHandler.getStringValue("userId");
        if (StringUtils.isEmpty(userId))
            return new ResponseEntity<>(new ApiResponseHandler("please provide userId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String displayName = apiRequestHandler.getStringValue("displayName");

        String secondaryEmail = apiRequestHandler.getStringValue("secondaryEmail");

        String alternateContactNo = apiRequestHandler.getStringValue("alternateContactNo");

        String languagePreference = apiRequestHandler.getStringValue("languagePreference");

        String timeZone = apiRequestHandler.getStringValue("timeZone");

        String businessRegistrationNo = apiRequestHandler.getStringValue("businessRegistrationNo");

        String businessType = apiRequestHandler.getStringValue("businessType");

        String country = apiRequestHandler.getStringValue("country");

        String state = apiRequestHandler.getStringValue("state");

        String city = apiRequestHandler.getStringValue("city");

        String postalCode = apiRequestHandler.getStringValue("postalCode");

        String address = apiRequestHandler.getStringValue("address");

        ApiResponseHandler apiResponseHandler = confidentialApiService.addClientDetails(userId, displayName, secondaryEmail, alternateContactNo, languagePreference, timeZone, businessRegistrationNo, businessType,
                country, state, city, postalCode, address);
        if (apiResponseHandler.getStatusCode() != 201)
            return new ResponseEntity<>(apiResponseHandler, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(apiResponseHandler, HttpStatus.CREATED);
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

    @PostMapping("/get-all-users")
    public ResponseEntity<ApiResponseHandler> getAllUsers(@RequestBody ApiRequestHandler apiRequestHandler){
        List<String> userIds = apiRequestHandler.getListValue("userIds", String.class);

        Integer index = apiRequestHandler.getIntegerValue("index");
        Integer itemPerIndex = apiRequestHandler.getIntegerValue("itemPerIndex");

        Boolean active = apiRequestHandler.getBooleanValue("active");

        List<String> roles = apiRequestHandler.getListValue("roles", String.class);

        Instant fromDate = apiRequestHandler.getInstantValue("fromDate");
        Instant toDate = apiRequestHandler.getInstantValue("toDate");

        String rawFromDate = apiRequestHandler.getStringValue("fromDate");
        if (rawFromDate != null && fromDate == null)
            return  new ResponseEntity<>(new ApiResponseHandler("please provide fromDate in yyyy-MM-ddTHH:MM:SS.nnnZ format", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        if (apiRequestHandler.getStringValue("toDate") != null && fromDate == null)
            return  new ResponseEntity<>(new ApiResponseHandler("please provide toDate in yyyy-MM-ddTHH:MM:SS.nnnZ", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        ApiResponseHandler allUsers = confidentialApiService.getAllUsers(index, itemPerIndex, userIds, roles, active, fromDate, toDate);
        return ResponseEntity.ok(allUsers);
    }
}

