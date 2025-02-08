package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.document.orderAndInvoice.Order;
import com.surgeRetail.surgeRetail.service.ConfidentialApiService;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
