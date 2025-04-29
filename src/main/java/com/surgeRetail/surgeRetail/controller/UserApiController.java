package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.service.UserApiService;
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
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserApiController {

    private final UserApiService userApiService;

    public UserApiController(UserApiService userApiService){
        this.userApiService = userApiService;
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

        ApiResponseHandler allUsers = userApiService.getAllUsers(index, itemPerIndex, userIds, roles, active, fromDate, toDate);
        return ResponseEntity.ok(allUsers);
    }

    @PostMapping("register-user-with-custom-roles")
    public ResponseEntity<ApiResponseHandler> registerUserWithCustomRoles(@RequestBody ApiRequestHandler apiRequestHandler){
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

        Set<String> roles = apiRequestHandler.getSetValue("roles", String.class);
        System.out.println(roles);
        if (CollectionUtils.isEmpty(roles))
            return new ResponseEntity<>(new ApiResponseHandler("please provide roles", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        ApiResponseHandler apiResponseHandler = userApiService.registerCustomUser(firstName, lastName, emailId, mobileNo, username, password, roles);
        if (apiResponseHandler.getStatusCode() != 201)
            return new ResponseEntity<>(apiResponseHandler, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponseHandler, HttpStatus.CREATED);
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

        ApiResponseHandler apiResponseHandler = userApiService.addClientDetails(userId, displayName, secondaryEmail, alternateContactNo, languagePreference, timeZone, businessRegistrationNo, businessType,
                country, state, city, postalCode, address);
        if (apiResponseHandler.getStatusCode() != 201)
            return new ResponseEntity<>(apiResponseHandler, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(apiResponseHandler, HttpStatus.CREATED);
    }

    @PostMapping("/client-details-by-user-id")
    public ResponseEntity<ApiResponseHandler> clientDetailsByClientId(@RequestBody ApiRequestHandler apiRequestHandler){
        String userId = apiRequestHandler.getStringValue("userId");
        if (StringUtils.isEmpty(userId))
            return new ResponseEntity<>(new ApiResponseHandler("please provide userId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        return userApiService.getClientByUserId(userId);
    }
}
