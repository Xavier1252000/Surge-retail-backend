package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.Item.Store;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClientDeskApiRepository {

    private final MongoTemplate mongoTemplate;

    public ClientDeskApiRepository(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public Store saveStore(Store store) {
        return mongoTemplate.save(store);
    }
}
