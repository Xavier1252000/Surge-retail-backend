package com.surgeRetail.surgeRetail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.surgeRetail.surgeRetail.repository.PermissionApiRepository;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.springframework.stereotype.Service;

@Service
public class PermissionApiService {

    private final PermissionApiRepository permissionApiRepository;
    private final ObjectMapper objectMapper;
    public PermissionApiService(PermissionApiRepository permissionApiRepository,
                                ObjectMapper objectMapper){
        this.permissionApiRepository = permissionApiRepository;
        this.objectMapper = objectMapper;
    }
    public ApiResponseHandler getUserPermissions(String userId) {
        ObjectNode node = objectMapper.createObjectNode();
        return new ApiResponseHandler("uer permissions", node, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, false);
    }
}
