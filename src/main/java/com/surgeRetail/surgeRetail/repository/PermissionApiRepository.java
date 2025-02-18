package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.permissions.ModulePermissions;
import com.surgeRetail.surgeRetail.document.permissions.Modules;
import com.surgeRetail.surgeRetail.document.permissions.Permissions;
import com.surgeRetail.surgeRetail.document.permissions.UserPermissions;
import com.surgeRetail.surgeRetail.utils.requestHandlers.ApiRequestHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PermissionApiRepository {

    private final MongoTemplate mongoTemplate;
    public PermissionApiRepository(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public UserPermissions getUserPermissionsByUserId(String userId){
        Query query = new Query();
        Criteria criteria  = new Criteria();
        criteria  = Criteria.where("userId").is(userId);
        query.addCriteria(criteria);
        return (UserPermissions) mongoTemplate.findOne(query, UserPermissions.class);
    }

    public UserPermissions saveOrUpdateUserPermissions(UserPermissions userPermissions) {
        return mongoTemplate.save(userPermissions);
    }

    public List<Modules> getAllModulesById(ArrayList<String> allModuleIds){
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(allModuleIds));
        return mongoTemplate.find(query, Modules.class);
    }

    public List<Permissions> getAllPermissionsById(ArrayList<String> allPermissionIds){
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(allPermissionIds));
        return mongoTemplate.findAll(Permissions.class);
    }

    public List<Modules> getAllModules(){
        Query query = new Query();
        return mongoTemplate.findAll(Modules.class);
    }

    public List<Permissions> getAllPermission(){
        return mongoTemplate.findAll(Permissions.class);
    }

    public Modules saveModule(Modules module) {
        return mongoTemplate.save(module);
    }

    public Permissions savePermission(Permissions permissions) {
        return mongoTemplate.save(permissions);
    }
}
