package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.service.PublicApiService;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/public")
@CrossOrigin(origins = "http://localhost:3000")
public class PublicApiController {

    private final PublicApiService publicApiService;

    public PublicApiController(PublicApiService publicApiService){
        this.publicApiService = publicApiService;
    }

    @PostMapping("/register-first-super-user")
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

        return publicApiService.registerSuperUser(firstName, lastName, username, emailId, mobileNo, password, superAdminSecret);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseHandler> login(@RequestBody Map<String, Object> requestMap) throws MessagingException {
        String username = (String) requestMap.get("username");
        if (StringUtils.isEmpty(username))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseHandler("authentication failed, wrong credentials", null, ResponseStatus.UNAUTHORIZED, ResponseStatusCode.UNAUTHORIZED, true));

        String password = (String) requestMap.get("password");
        if (StringUtils.isEmpty(password))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseHandler("authentication failed, wrong credentials", null, ResponseStatus.UNAUTHORIZED, ResponseStatusCode.UNAUTHORIZED, true));

        return publicApiService.authenticateUser(username, password);
    }
}
