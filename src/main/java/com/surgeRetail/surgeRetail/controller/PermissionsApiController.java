package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.service.PermissionApiService;
import com.surgeRetail.surgeRetail.utils.requestHandlers.ApiRequestHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permissions")
public class PermissionsApiController {
    private final PermissionApiService permissionApiService;

    public PermissionsApiController(PermissionApiService permissionApiService){
        this.permissionApiService = permissionApiService;
    }

    @PostMapping("/get-user-permissions")
    public ApiResponseHandler getUserPermissions(@RequestBody ApiRequestHandler apiRequestHandler){
        String userId = apiRequestHandler.getStringValue("userId");
        if (StringUtils.isEmpty(userId))
            return new ApiResponseHandler("no user found with provided id", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        return permissionApiService.getUserPermissions(userId);
    }
}
