package com.surgeRetail.surgeRetail.utils.responseHandlers;

import com.fasterxml.jackson.annotation.JsonProperty;
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
}
