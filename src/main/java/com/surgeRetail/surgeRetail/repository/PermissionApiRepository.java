package com.surgeRetail.surgeRetail.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionApiRepository {

    private final MongoTemplate mongoTemplate;
    public PermissionApiRepository(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }
}
