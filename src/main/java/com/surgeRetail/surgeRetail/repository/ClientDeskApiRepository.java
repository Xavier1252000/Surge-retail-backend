package com.surgeRetail.surgeRetail.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClientDeskApiRepository {

    private final MongoTemplate mongoTemplate;

    public ClientDeskApiRepository(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }
}
