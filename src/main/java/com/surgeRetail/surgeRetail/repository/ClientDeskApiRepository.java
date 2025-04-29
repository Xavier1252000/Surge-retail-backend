package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.store.Store;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    public Store findStoreByStoreAdminId(String storeAdminId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("storeAdminIds").is(storeAdminId));
        return mongoTemplate.findOne(query, Store.class);
    }
}
