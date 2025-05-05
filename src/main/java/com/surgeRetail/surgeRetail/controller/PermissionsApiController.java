package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.document.permissions.ModulePermissions;
import com.surgeRetail.surgeRetail.document.permissions.Modules;
import com.surgeRetail.surgeRetail.service.PermissionApiService;
import com.surgeRetail.surgeRetail.utils.requestHandlers.ApiRequestHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionsApiController {
    private final PermissionApiService permissionApiService;

    public PermissionsApiController(PermissionApiService permissionApiService){
        this.permissionApiService = permissionApiService;
    }

    @PostMapping("/add-modules")
    public ApiResponseHandler addModules(@RequestBody ApiRequestHandler apiRequestHandler){
        Modules module = apiRequestHandler.getGenericObjectValue("modules", Modules.class);
        if (module == null)
            return new ApiResponseHandler("please provide modules", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        if (StringUtils.isEmpty(module.getName()))
            return new ApiResponseHandler("please provide name",module, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true );

        return permissionApiService.addModule(module);
    }

    @PostMapping("/add-user-permission")
    public ApiResponseHandler addUserPermission(@RequestBody ApiRequestHandler apiRequestHandler){
        String userId = apiRequestHandler.getStringValue("userId");
        if(StringUtils.isEmpty(userId))
            return new ApiResponseHandler("please provide userId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        List<ModulePermissions> modulePermissionsList = apiRequestHandler.getListValue("modulesPermissions", ModulePermissions.class);
        System.out.println(modulePermissionsList);

        if(CollectionUtils.isEmpty(modulePermissionsList))
            return new ApiResponseHandler("Please provide module permissions", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        if(!CollectionUtils.isEmpty(modulePermissionsList)){
            for(int i=0;i<modulePermissionsList.size();i++){
                ModulePermissions modulePermissions = modulePermissionsList.get(i);
                String moduleId = modulePermissions.getModuleId();
                int a = i+1;
                if(StringUtils.isEmpty(moduleId)){
                    return new ApiResponseHandler(a+"th module is invalid", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
                }
                List<String> permessionIds = modulePermissions.getPermissionIds();
                if(CollectionUtils.isEmpty(permessionIds)){
                    return new ApiResponseHandler("no permissions for module no : "+a, null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
                }
                for (String permissionId : permessionIds) {
                    if (StringUtils.isEmpty(permissionId)) {
                        return new ApiResponseHandler("invalid permissions for module"+a, null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
                    }
                }
            }}
        return permissionApiService.addUserPermission(userId,modulePermissionsList);
    }

    @PostMapping("/update-user-permission")
    public ApiResponseHandler updateUserPermissions(@RequestBody ApiRequestHandler apiRequestHandler){

        String userId = apiRequestHandler.getStringValue("userId");
        if(StringUtils.isEmpty(userId))
            return new ApiResponseHandler("please provide modulePermissions", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        List<ModulePermissions> modulePermissionsList =  apiRequestHandler.getListValue("modulesPermissions",ModulePermissions.class);
        if(CollectionUtils.isEmpty(modulePermissionsList)){
            return new ApiResponseHandler("please provide modulePermissions", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }
        return permissionApiService.updateUserPermissions(userId,modulePermissionsList);
    }

    @PostMapping("/get-user-permissions")
    public ApiResponseHandler getUserPermissions(@RequestBody ApiRequestHandler apiRequestHandler){
        String userId = apiRequestHandler.getStringValue("userId");
        if (StringUtils.isEmpty(userId))
            return new ApiResponseHandler("please provide userId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        return permissionApiService.getUserPermissions(userId);
    }

    @GetMapping("/get-all-modules")
    public ApiResponseHandler getAllModules(){
        return permissionApiService.getAllModules();
    }

    @PostMapping("/create-permission")
    public ApiResponseHandler createPermission(@RequestBody ApiRequestHandler apiRequestHandler){
        String name = apiRequestHandler.getStringValue("name");
        if (StringUtils.isEmpty(name))
            return new ApiResponseHandler("please provide name", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String description = apiRequestHandler.getStringValue("description");
        return permissionApiService.createPermission(name, description);
    }

    @GetMapping("/get-all-permissions")
    public ApiResponseHandler getAllPermissions(){
        return permissionApiService.getAllPermissions();
    }

}
