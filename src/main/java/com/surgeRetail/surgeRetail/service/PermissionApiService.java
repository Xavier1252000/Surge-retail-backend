package com.surgeRetail.surgeRetail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.surgeRetail.surgeRetail.document.permissions.ModulePermissions;
import com.surgeRetail.surgeRetail.document.permissions.Modules;
import com.surgeRetail.surgeRetail.document.permissions.Permissions;
import com.surgeRetail.surgeRetail.document.permissions.UserPermissions;
import com.surgeRetail.surgeRetail.repository.PermissionApiRepository;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionApiService {

    private final PermissionApiRepository permissionApiRepository;
    private final ObjectMapper objectMapper;
    public PermissionApiService(PermissionApiRepository permissionApiRepository,
                                ObjectMapper objectMapper){
        this.permissionApiRepository = permissionApiRepository;
        this.objectMapper = objectMapper;
    }


    public ApiResponseHandler addUserPermission(String userId, List<ModulePermissions> modulePermissionsList)
    {
        UserPermissions userPermissions = permissionApiRepository.getUserPermissionsByUserId(userId);
        if(userPermissions != null){
            return new ApiResponseHandler("Provided userId is already exist",null,ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST,true);
        }
        List<Modules> allModules = permissionApiRepository.getAllModules();
        List<String> allModulesIds = allModules.stream().map(Modules::getId).toList();
        List<Permissions> allPermission = permissionApiRepository.getAllPermission();
        List<String> allPermissionIds = allPermission.stream().map(Permissions::getId).toList();
        UserPermissions userPermissionss = new UserPermissions();
        userPermissionss.setUserId(userId);
        List<ModulePermissions> modulePermissionsList1 = new ArrayList<>();
        for(int i=0;i<modulePermissionsList.size();i++)
        {
            ModulePermissions modulePermissions = new ModulePermissions();
            ModulePermissions modulePermissionss = modulePermissionsList.get(i);
            String moduleId = modulePermissionss.getModuleId();
            boolean ModuleIdStatus = allModulesIds.contains(moduleId);
            if(!ModuleIdStatus){
                return new ApiResponseHandler("Invalid moduleId: "+moduleId,null,ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST,true);
            }
            List<String> permessionIds = modulePermissionss.getPermissionIds();
            for(int j=0;j<permessionIds.size();j++){
                String permissionId = permessionIds.get(j);
                boolean permissionIdStatus = allPermissionIds.contains(permissionId);
                if(!permissionIdStatus){
                    return new ApiResponseHandler("Invalid permessionId: "+permissionId,null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST,true);
                }
            }
            modulePermissions.setModuleId(moduleId);
            modulePermissions.setPermissionIds(permessionIds);
            modulePermissionsList1.add(modulePermissions);
            userPermissionss.setModulesPermissions(modulePermissionsList1);
        }
        UserPermissions userPermissions1 = permissionApiRepository.saveOrUpdateUserPermissions(userPermissionss);
        return new ApiResponseHandler("success",userPermissions1,ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS,false);
    }

    public ApiResponseHandler updateUserPermissions(String userId, List<ModulePermissions> modulePermissionsList){
        UserPermissions userPermissions = permissionApiRepository.getUserPermissionsByUserId(userId);
        if(userPermissions == null){
            return new ApiResponseHandler("Provided userId did not exist",null, ResponseStatus.BAD_REQUEST,ResponseStatusCode.BAD_REQUEST, true);
        }
        Set<String> allModuleIds = modulePermissionsList.stream().map(ModulePermissions::getModuleId).collect(Collectors.toSet());
        List<Modules> allModulesById = permissionApiRepository.getAllModulesById(new ArrayList<>(allModuleIds));
        if(CollectionUtils.isEmpty(allModulesById)){
            return new ApiResponseHandler("Please provide valid moduleId",null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }
        Set<String> allPermissionIds = modulePermissionsList.stream().flatMap(modulePermissions -> modulePermissions.getPermissionIds().stream())
                .collect(Collectors.toSet());
        List<Permissions> allPermissionsById = permissionApiRepository.getAllPermissionsById(new ArrayList<>(allPermissionIds));
        if(CollectionUtils.isEmpty(allPermissionsById)) {
            return new ApiResponseHandler("Please provide valid permissionIds",null,ResponseStatus.BAD_REQUEST,ResponseStatusCode.BAD_REQUEST, true);
        }
        userPermissions.setUserId(userId);
        List<ModulePermissions> modulePermissionsList1 = new ArrayList<>();
        Set<String> allPermisionId = allPermissionsById.stream().map(Permissions::getId).collect(Collectors.toSet());

        for (ModulePermissions e : modulePermissionsList) {
            String moduleId = e.getModuleId();
            Optional<Modules> module = allModulesById.stream().filter(modules -> modules.getId().equalsIgnoreCase(moduleId)).findFirst();
            if (module.isEmpty()) {
                return new ApiResponseHandler("moduleId does not exist "+moduleId, null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
            }
            List<String> permissionIds = e.getPermissionIds();
            boolean b = allPermisionId.containsAll(permissionIds);
            Set<String> missingPermissionIds = permissionIds.stream().filter(permissionId -> !allPermisionId.contains(permissionId)).collect(Collectors.toSet());
            if(!missingPermissionIds.isEmpty()){
                return new ApiResponseHandler("permissionIds does not exist "+missingPermissionIds, null, ResponseStatus.BAD_REQUEST,ResponseStatusCode.BAD_REQUEST, true);
            }
            ModulePermissions modulePermissions = new ModulePermissions();
            modulePermissions.setModuleId(moduleId);
            modulePermissions.setPermissionIds(permissionIds);
            modulePermissionsList1.add(modulePermissions);

        }
        userPermissions.setModulesPermissions(modulePermissionsList1);
        userPermissions = permissionApiRepository.saveOrUpdateUserPermissions(userPermissions);
        return new ApiResponseHandler("successfully updated userPermissions",userPermissions, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }

    public ApiResponseHandler addModule(Modules module) {
        module.onCreate();
        Modules savedModule = permissionApiRepository.saveModule(module);
        return new ApiResponseHandler("Module Created",savedModule, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }
}
