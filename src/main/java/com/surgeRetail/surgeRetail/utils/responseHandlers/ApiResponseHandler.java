package com.surgeRetail.surgeRetail.utils.responseHandlers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

@ResponseBody
public class ApiResponseHandler {

    @JsonProperty("response")
    private Map<String, Object> response = new LinkedHashMap<>();
    ProjectDetails projectDetails = new ProjectDetails();

    public ApiResponseHandler(String message, Object data, String status, int statusCode, boolean error){
        response.put("project details", this.projectDetails);
        response.put("message", message);
        response.put("status", status);
        response.put("statusCode", statusCode);
        response.put("data", data);
        response.put("error", error);

    }

    public static ResponseEntity<ApiResponseHandler> createResponse(String message, Object data, int statusCode){

        String status = null;
        boolean isError = true;

        switch (statusCode) {
            case 200:
                status = ResponseStatus.SUCCESS;
                break;
            case 201:
                status = ResponseStatus.CREATED;
                break;
            case 400:
                status = ResponseStatus.BAD_REQUEST;
                break;
            case 401:
                status = ResponseStatus.UNAUTHORIZED;
                break;
            case 402:
                status = ResponseStatus.PAYMENT_REQUIRED;
                break;
            case 403:
                status = ResponseStatus.FORBIDDEN;
                break;
            case 404:
                status = ResponseStatus.NOT_FOUND;
                break;
            case 405:
                status = ResponseStatus.METHOD_NOT_ALLOWED;
                break;
            case 500:
                status = ResponseStatus.INTERNAL_SERVER_ERROR;
                break;
            case 504:
                status = ResponseStatus.GATEWAY_TIMEOUT;
                break;
            default:
                status = ResponseStatus.UNKNOWN;
        }

        if (statusCode == 200 || statusCode == 201){
            isError = false;
        }
        return new ResponseEntity<>(new ApiResponseHandler(message, data,status, statusCode, isError ), HttpStatus.valueOf(statusCode));
    }

    public int getStatusCode(){
        return Integer.parseInt(String.valueOf(response.get("statusCode")));
    }
}
