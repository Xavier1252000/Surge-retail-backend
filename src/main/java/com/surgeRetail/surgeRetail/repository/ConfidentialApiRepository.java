package com.surgeRetail.surgeRetail.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ConfidentialApiRepository {

    private final MongoTemplate mongoTemplate;


    public ConfidentialApiRepository(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }
}
